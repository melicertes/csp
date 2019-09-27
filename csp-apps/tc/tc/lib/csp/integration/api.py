from __future__ import absolute_import, unicode_literals

import logging
import uuid
import json

from rest_framework import views
from rest_framework import serializers
from rest_framework.response import Response
from rest_framework.exceptions import ValidationError

from django.core import serializers as model_serializers
from django.core.serializers.base import DeserializationError
from django.apps import apps
from django.db import IntegrityError

from csp.central.models import Team, TrustCircle
from csp.contacts.models import IncomingTeamContact

from .models import ChangeLog


AUDITLOG = logging.getLogger('ctc')
log = logging.getLogger()


AUTHORITATIVE_MODELS = (
    'central.trustcircle',
    'central.team',
    'common.country',
)


class RecordIdField(serializers.Field):
    def to_representation(self, obj):
        model_name, model_pk = obj
        return '{}:{}'.format(model_name, model_pk)

    def to_internal_value(self, data):
        try:
            model_name, model_pk = data.split(':')
            model_pk = uuid.UUID(model_pk)
        except (ValueError, AttributeError):
            raise ValidationError('Invalid recordId format')
        if (model_name not in AUTHORITATIVE_MODELS and
                model_name != 'contacts.teamcontact'):
            raise ValidationError('Invalid model name')
        try:
            apps.get_model(model_name)
        except LookupError:
            raise ValidationError('Invalid recordId')
        return (model_name, model_pk)


class DataParamsSerializer(serializers.Serializer):
    cspId = serializers.CharField(required=True)
    applicationId = serializers.CharField(required=True)
    recordId = RecordIdField(required=True)
    originCspId = serializers.CharField(required=False)
    originApplicationId = serializers.CharField(required=False)
    originRecordId = RecordIdField(required=False)
    dateTime = serializers.DateTimeField()


class SharingParamsSerializer(serializers.Serializer):
    toShare = serializers.BooleanField(required=True)
    isExternal = serializers.BooleanField(required=True)
    trustCircleId = serializers.ListField(child=serializers.CharField(), required=False, allow_null=True)
    teamId = serializers.ListField(child=serializers.CharField(), required=False, allow_null=True)


class IntegrationDataSerializer(serializers.Serializer):
    dataParams = DataParamsSerializer(required=True)
    sharingParams = SharingParamsSerializer(required=True)
    dataType = serializers.CharField(required=True)
    dataObject = serializers.JSONField(allow_null=True)


class AdapterView(views.APIView):
    def post(self, request):
        return self.create_or_update(request)

    def put(self, request):
        return self.create_or_update(request)

    def delete(self, request):
        serializer = IntegrationDataSerializer(data=request.data)
        if serializer.is_valid(raise_exception=True):
            data = serializer.validated_data
            auditlog(data, 'delete')
            model_name, model_pk = data['dataParams']['recordId']
            model = apps.get_model(model_name)
            try:
                obj = model.objects.get(pk=model_pk)
            except model.DoesNotExist:
                raise ValidationError('Record not found')

            # deletes that come in via the adapter API should always
            # have toShare set to False when sending it back to the
            # integration layer
            obj.to_share = False
            obj.delete()

        return Response({'detail': 'Record deleted'})

    def create_or_update(self, request):
        serializer = IntegrationDataSerializer(data=request.data)
        if serializer.is_valid():
            data = serializer.validated_data
            auditlog(data, 'create_or_update')
            model_name, model_pk = data['dataParams']['recordId']

            # if its an authoritatitve model, then it must come from
            # ENISA... security by definition
            if model_name in AUTHORITATIVE_MODELS:
                deserialized = self.deserialize(model_name, model_pk, data['dataObject'])

                # if this change comes from an external CSP, then the change should
                # be sent out again with toShare = False. The to_share attribute is
                # is examined in the ChangeLogManager.log method
                if data['sharingParams']['isExternal']:
                    deserialized.object.to_share = False
                try:
                    deserialized.save()
                except IntegrityError as e:
                    log.exception('Error during save operation')
                    raise ValidationError(e)

            # otherwise it's contact data, so we record it for the user
            # to integrate manually or discard
            elif model_name == 'contacts.teamcontact':
                try:
                    itc = IncomingTeamContact.objects.create(
                        csp_id=data['dataParams']['cspId'],
                        app_id=data['dataParams']['applicationId'],
                        data_object=json.loads(data['dataObject']),
                        target_circle_id=data['sharingParams'].get('trustCircleId', []) or [],
                        target_team_id=data['sharingParams'].get('teamId', []) or [])
                except Exception as e:
                    log.exception('Error while saving incoming contact data')
                    raise ValidationError(str(e))

                # Should we have a Team matching IncomingTeamContact on file
                # And the source csp_id matches the csp_id saved in Team,
                # we brutally update all non ENISA information provided.
                # This happens by setting team_selfinfo JSONField to
                # serialized TeamContact data.
                # raise Exception(itc.deserialized.short_name, itc.deserialized.country)
                try:
                    team = Team.objects.get(
                        short_name=itc.deserialized.short_name,
                        country=itc.deserialized.country)
                    if team.csp_id == itc.deserialized.csp_id:
                        team.team_selfinfo = itc.data_object
                        team.save()
                except Team.DoesNotExist:
                    pass

            return Response({'detail': 'Record saved'})
        log.error('Invalid JSON data: {}'.format(serializer.errors))
        raise ValidationError('Invalid JSON data')

    def deserialize(self, model_name, model_pk, data):
        try:
            deserialized = list(model_serializers.deserialize('json', data))[0]
        except (IndexError, DeserializationError, AttributeError):
            log.exception('Invalid data object')
            raise ValidationError('Invalid dataObject')

        deserialized_model_name = '{}.{}'.format(
            deserialized.object._meta.app_label,
            deserialized.object._meta.model_name)

        if (deserialized_model_name != model_name or
                deserialized.object.pk != model_pk):
            log.exception('Invalid dataObject model')
            raise ValidationError('Invalid dataObject model')

        return deserialized


class ExportView(views.APIView):
    """
    Writes all data out to ChangeLog table, annotated with a target circle
    'CTC::CSP_ALL'.
    """
    def post(self, request):
        AUDITLOG.info('Nuke-It! pressed, sharing Teams and TrustCircles with CTC::CSP_ALL')
        models = (Team, TrustCircle)
        for model in models:
            for obj in model.objects.all():
                ChangeLog.objects.log('create', obj, ['CTC::CSP_ALL'])

        return Response({'success': True})


def auditlog(data, action):
    try:
        csp_id = data['dataParams']['cspId']
    except:
        csp_id = 'PARSE_ERROR'

    try:
        app_id = data['dataParams']['applicationId']
    except:
        app_id = 'PARSE ERROR'

    try:
        model_name, model_pk = data['dataParams']['recordId']
    except:
        model_name, model_pk = ('PARSE ERROR', 'PARSE_ERROR')

    AUDITLOG.info('IL.{} {} request from {} for {}<{}>'.format(
        app_id, action, csp_id, model_name, model_pk))
