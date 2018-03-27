from __future__ import absolute_import

from rest_framework import serializers
from rest_framework import viewsets
from django_filters import rest_framework as filters


from csp.central.models import Team as CentralTeam

from .models import (
    TeamContact,
    TeamMembership,
    TeamPhoneNumber,
    TeamKeyOrCertificate,
    TeamMemberMembership,
    TeamMemberPhoneNumber,
    TeamMemberKeyOrCertificate,
    PersonContact,
    TeamMember,
    LocalTrustCircle,
)


class CertificateSerializer(serializers.Serializer):
    tag = serializers.CharField(allow_blank=True, allow_null=True)
    method = serializers.CharField(allow_blank=True, allow_null=True)
    keyid = serializers.CharField(allow_blank=True, allow_null=True)
    visibility = serializers.CharField(allow_blank=True, allow_null=True)
    data = serializers.CharField(allow_blank=True, allow_null=True)


class MembershipSerializer(serializers.Serializer):
    organisation = serializers.CharField(allow_blank=True, allow_null=True)
    membership_state = serializers.CharField(allow_blank=True, allow_null=True)
    since = serializers.CharField(allow_blank=True, allow_null=True)


class PhoneNumberSerializer(serializers.Serializer):
    tag = serializers.CharField(allow_blank=True, allow_null=True)
    number = serializers.CharField(allow_blank=True, allow_null=True)
    timezone = serializers.CharField(allow_blank=True, allow_null=True)
    number_details = serializers.CharField(allow_blank=True, allow_null=True)
    visibility = serializers.CharField(allow_blank=True, allow_null=True)


class PersonContactSerializer(serializers.ModelSerializer):
    phone_numbers = PhoneNumberSerializer(many=True, allow_null=True)
    certificates = CertificateSerializer(many=True, allow_null=True)
    memberships = MembershipSerializer(many=True, allow_null=True)

    class Meta:
        model = PersonContact
        fields = '__all__'


class TeamMemberSerializer(serializers.ModelSerializer):
    phone_numbers = PhoneNumberSerializer(many=True, allow_null=True)
    certificates = CertificateSerializer(many=True, allow_null=True)
    memberships = MembershipSerializer(many=True, allow_null=True)

    class Meta:
        model = TeamMember
        exclude = ('id', 'team')


class TeamContactSerializer(serializers.ModelSerializer):
    team_members = TeamMemberSerializer(many=True, allow_null=True)
    phone_numbers = PhoneNumberSerializer(many=True, allow_null=True)
    certificates = CertificateSerializer(many=True, allow_null=True)
    memberships = MembershipSerializer(many=True, allow_null=True)
    csp_id = serializers.CharField(allow_blank=True, allow_null=True)

    class Meta:
        model = TeamContact
        fields = (
            'additional_countries',
            'automated_email',
            'automated_email_format',
            'business_hours',
            'business_hours_timezone',
            'certificates',
            'constituency_asns',
            'constituency_description',
            'constituency_domains',
            'constituency_ipranges',
            'constituency_types',
            'contact_postal_address',
            'contact_postal_country',
            'country',
            'csp_domain',
            'csp_id',
            'csp_installed',
            'established',
            'host_organisation',
            'id',
            'main_email',
            'member_locations',
            'memberships',
            'name',
            'nis_sectors',
            'nis_team_types',
            'phone_numbers',
            'public_email',
            'public_ftp',
            'public_mailinglist',
            'public_usenet',
            'public_www',
            'scope_asns',
            'scope_email',
            'scope_ipranges',
            'short_name',
            'status',
            'team_members',
        )

    def as_object(self):
        """
        Create the deserialized data as fake in-memory objects
        """
        return DeserializedTeamContact(**self.validated_data)


class FakeRelated(list):
    def all(self):
        return self

    def exists(self):
        return len(self) > 0


class DeserializedObject(object):
    _klass = None
    _name = None
    _related_objects = {}

    def __init__(self, **data):
        for name, klass in self._related_objects.items():
            related = FakeRelated()
            for entry in data.pop(name, []):
                related.append(klass(**entry))
            setattr(self, name, related)
        self._obj = self._klass(**data)

    def __getattr__(self, name):
        return getattr(self._obj, name)

    def save(self):
        raise RuntimeError('Cannot save deserialized objects!')

    def __unicode__(self):
        return unicode(self._obj)

    def __str__(self):
        return str(self._obj)


class DeserializedTeamMember(DeserializedObject):
    _klass = TeamMember
    _name = 'teammember'

    _related_objects = {
        'phone_numbers': TeamMemberPhoneNumber,
        'certificates': TeamMemberKeyOrCertificate,
        'memberships': TeamMemberMembership,
    }


class DeserializedTeamContact(DeserializedObject):
    _klass = TeamContact
    _name = 'teamcontact'

    _related_objects = {
        'team_members': DeserializedTeamMember,
        'phone_numbers': TeamPhoneNumber,
        'certificates': TeamKeyOrCertificate,
        'memberships': TeamMembership,
    }


class LocalTrustCircleSerializer(serializers.ModelSerializer):
    teams = serializers.SerializerMethodField('get_central_team_pks')

    class Meta:
        model = LocalTrustCircle
        fields = (
            'id',
            'short_name',
            'name',
            'description',
            'auth_source',
            'info_url',
            'membership_url',
            'created',
            'tlp',
            'teams',
            'team_contacts',
            'person_contacts',
        )

    def get_central_team_pks(self, obj):
        """
        Return the primary key of all *central* teams that are in CTCs
        included in this LTC and all central teams are are direct members
        of this LTC
        """
        ctc_teams = (CentralTeam.objects
                     .filter(circles__in=obj.trustcircles.all())
                     .order_by())
        return obj.teams.order_by().union(ctc_teams).values_list('id', flat=True)


class LocalTrustCircleViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = LocalTrustCircle.objects.all()
    serializer_class = LocalTrustCircleSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_fields = ['short_name']


class PersonContactViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = PersonContact.objects.all()
    serializer_class = PersonContactSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_fields = ['full_name', 'email']


class TeamContactFilters(filters.FilterSet):
    country = filters.CharFilter(lookup_expr='iexact')
    short_name = filters.CharFilter(lookup_expr='iexact')
    email = filters.CharFilter(name='main_email', lookup_expr='iexact')

    class Meta:
        model = TeamContact
        fields = ('country', 'short_name', 'email')


class TeamContactViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = TeamContact.objects.all()
    serializer_class = TeamContactSerializer
    filter_backends = (filters.DjangoFilterBackend,)
    filter_class = TeamContactFilters
