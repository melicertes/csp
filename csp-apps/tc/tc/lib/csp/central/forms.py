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
            'nis_sectors': Suggest(source='nis_sector'),
            'nis_team_types': Suggest(source='nis_team_type'),
            'status': Suggest(source='team_status'),
            'country': Suggest(source='country'),
            'additional_countries': SuggestMultiple(source='country'),
        }


class CircleForm(AnnotatedModelForm):
    class Meta:
        model = TrustCircle
        fields = ('teams', 'short_name', 'name', 'description', 'auth_source',
                  'info_url', 'membership_url', 'teams', 'tlp')
