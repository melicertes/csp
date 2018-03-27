from __future__ import absolute_import

import uuid

from django.shortcuts import get_object_or_404

from rest_framework import viewsets
from rest_framework import serializers

from .models import Team, TrustCircle


class TrustCircleSerializer(serializers.ModelSerializer):
    teams = serializers.PrimaryKeyRelatedField(
        queryset=Team.objects.all(),
        many=True)

    class Meta:
        model = TrustCircle
        fields = ('id', 'short_name', 'name', 'description', 'auth_source',
                  'info_url', 'membership_url', 'teams', 'created')
        read_only_fields = ('id', 'created')


class TeamSerializer(serializers.ModelSerializer):
    class Meta:
        model = Team
        fields = ('id', 'short_name', 'name', 'host_organisation', 'description',
                  'country', 'additional_countries', 'established',
                  'nis_team_types', 'nis_sectors', 'created', 'csp_installed',
                  'csp_id', 'csp_domain', 'status')
        read_only_fields = ('id', 'created')


class TrustCircleViewSet(viewsets.ModelViewSet):
    queryset = TrustCircle.objects.all()
    serializer_class = TrustCircleSerializer

    def get_object(self):
        """
        Enable fetching of trust circles both by uuid and short_name
        """
        queryset = self.filter_queryset(self.get_queryset())

        try:
            value = uuid.UUID(self.kwargs['pk'])
            field = 'pk'
        except ValueError:
            value = self.kwargs['pk']
            field = 'short_name'

        filter_kwargs = {field: value}
        obj = get_object_or_404(queryset, **filter_kwargs)

        self.check_object_permissions(self.request, obj)

        return obj


class TeamViewSet(viewsets.ModelViewSet):
    queryset = Team.objects.all()
    serializer_class = TeamSerializer
