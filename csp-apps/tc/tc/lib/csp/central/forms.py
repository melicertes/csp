from django.forms import Textarea
from csp.common.forms import AnnotatedModelForm, Suggest, SuggestMultiple
from csp.central.models import Team, TrustCircle


class TeamForm(AnnotatedModelForm):
    class Meta:
        model = Team
        fields = ('short_name', 'name', 'country', 'additional_countries',
                  'nis_sectors', 'status', 'established', 'csp_installed',
                  'nis_team_types', 'host_organisation', 'description',
                  'csp_id', 'csp_domain')

        widgets = {
            'nis_sectors': SuggestMultiple(source='nis_sector'),
            'nis_team_types': SuggestMultiple(source='nis_team_type'),
            'status': Suggest(source='team_status'),
            'country': Suggest(source='country'),
            'additional_countries': SuggestMultiple(source='country'),
            'description': Textarea(attrs={'rows': 10, 'cols': 40}),
        }


class CircleForm(AnnotatedModelForm):
    class Meta:
        model = TrustCircle
        fields = ('teams', 'short_name', 'name', 'description', 'auth_source',
                  'info_url', 'membership_url', 'teams', 'tlp')

        widgets = {
            'description': Textarea(attrs={'rows': 12, 'cols': 40}),
        }
