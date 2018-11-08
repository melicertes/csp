from __future__ import absolute_import

import logging
import json

from requests import Session

from django.apps import apps
from django.core import serializers
from django.utils.formats import date_format

LOG = logging.getLogger('dsl_dispatch')


class DSL(object):
    def __init__(self, endpoint_url, cert_file, key_file, cachain_file, csp_id, app_id):
        self.endpoint_url = endpoint_url
        self.csp_id = csp_id
        self.app_id = app_id

        self.api = Session()
        if cert_file and key_file:
            self.api.cert = (cert_file, key_file)
        self.api.verify = cachain_file

    def dispatch_change(self, change):
        data = self.change_to_json(change)

        if change.action == 'create':
            r = self.api.post(self.endpoint_url, json=data)
        elif change.action == 'update':
            r = self.api.put(self.endpoint_url, json=data)
        elif change.action == 'delete':
            r = self.api.delete(self.endpoint_url, json=data)

        if r.status_code != 200:
            raise RuntimeError('Unable to synchronize {} on {}:{}: {}'.format(
                change.action, change.model_name, change.model_pk, r.status_code))

    def change_to_json(self, change):
        if change.model_name == 'contacts.teamcontact':
            model = apps.get_model(change.model_name)
            obj = model.objects.get(pk=change.model_pk)
            from csp.contacts.api import TeamContactSerializer
            data = json.dumps(TeamContactSerializer(obj).data)
            datatype = 'contact'
        else:
            datatype = 'trustCircle'
            if change.action == 'delete':
                data = {}
            else:
                model = apps.get_model(change.model_name)
                obj = model.objects.get(pk=change.model_pk)
                data = serializers.serialize('json', [obj])

        params = {
            'dataParams': {
                'cspId': self.csp_id,
                'originCspId': self.csp_id,
                'applicationId': self.app_id,
                'originApplicationId': self.app_id,
                'recordId': '{}:{}'.format(change.model_name, change.model_pk),
                'originRecordId': '{}:{}'.format(change.model_name, change.model_pk),
                'dateTime': _js_ts(change.created),
            },
            'sharingParams': {
                'toShare': change.to_share,
                'isExternal': False,
            },
            'dataType': datatype,
            'dataObject': data,
        }

        if change.target_trustcircles:
            params['sharingParams']['trustCircleId'] = change.target_trustcircles

        if change.target_teams:
            params['sharingParams']['teamId'] = change.target_teams

        return params


def _js_ts(dte):
    return date_format(dte, 'Y-m-d') + 'T' + date_format(dte, 'H:i:sO')
