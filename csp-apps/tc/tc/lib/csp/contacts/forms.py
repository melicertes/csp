from django.forms.models import BaseInlineFormSet, inlineformset_factory
from django import forms

from csp.common.forms import (
    AnnotatedForm,
    AnnotatedModelForm,
    Suggest,
    SuggestMultiple)

from .models import (
    TeamContact,
    TeamPhoneNumber,
    TeamKeyOrCertificate,
    TeamMembership,
    PersonContact,
    PersonPhoneNumber,
    PersonKeyOrCertificate,
    PersonMembership,
    TeamMember,
    TeamMemberPhoneNumber,
    TeamMemberKeyOrCertificate,
    TeamMemberMembership,
    OutbandAlertingContact,
    OutbandAlertingAccess,
    LocalTrustCircle)
from csp.central.models import TrustCircle, Team


# Team
class TeamContactBaseForm(AnnotatedModelForm):
    readonly = ['outband_alerting_id']
    show_blocks = ['csp_team', 'team']

    class Meta:
        # Fields that should be single line are TextInput.
        # Most fields are saved as TextField in DB (max_length=infinity).
        model = TeamContact
        fields = '__all__'

        # All widget definitions (also for subclasses) should go here:
        widgets = {
            'short_name': forms.TextInput,
            'name': forms.TextInput,
            'host_organisation': forms.TextInput,
            'nis_sectors': Suggest(source='nis_sector'),
            'nis_team_types': Suggest(source='nis_team_type'),
            'status': Suggest(source='team_status'),
            'country': Suggest(source='country'),
            'constituency_types': SuggestMultiple(source='constituency_type'),
            'member_locations': SuggestMultiple(source='country'),
            'reactive_services': SuggestMultiple(source='reactive_service'),
            'proactive_services': SuggestMultiple(source='proactive_service'),
            'quality_management': SuggestMultiple(source='quality_management'),
            'additional_countries': SuggestMultiple(source='country'),
            'contact_postal_country': Suggest(source='country'),
            'constituency_description': forms.TextInput,
            'description': forms.widgets.Textarea(attrs={'rows': 5,
                                                         'cols': 40}),
            'public_www': forms.TextInput,
            'public_ftp': forms.TextInput,
            'public_mailinglist': forms.TextInput,
            'public_usenet': forms.TextInput,
            'business_hours': forms.TextInput,
            'outside_business_hours': forms.TextInput,
            'business_hours_timezone': forms.TextInput,
            'billing_postal_country': Suggest(source='country'),
            'vat_number': forms.TextInput,
            'url_rfc2350': forms.TextInput,
            'process_tool': forms.TextInput,
            'related_software': forms.TextInput,
            'generic': forms.TextInput,
            'os': forms.TextInput,
            'platform': forms.TextInput,
            'network': forms.TextInput,
            'other': forms.TextInput,
            'references': forms.TextInput,
            'accreditations': forms.TextInput,
            'projects': forms.TextInput,
            'reporting_structure': forms.TextInput,
            'education': forms.TextInput,
            'headcount_normal': forms.TextInput,
            'headcount_backup': forms.TextInput,
            'fte_normal': forms.TextInput,
            'fte_backup': forms.TextInput,
            'scope_asns': forms.TextInput,
        }

    def clean_csp_id(self):
        self.cleaned_data['csp_id'] = ''
        return ''

    def clean_csp_domain(self):
        self.cleaned_data['csp_domain'] = ''
        return ''

    def clean_csp_installed(self):
        self.cleaned_data['csp_installed'] = False
        return False

    def clean_nis_team_types(self):
        self.cleaned_data['nis_team_types'] = []
        return []

    def clean_nis_sectors(self):
        self.cleaned_data['nis_sectors'] = []
        return []

    def clean_status(self):
        self.cleaned_data['status'] = ''
        return ''


class TeamContactTITUSForm(TeamContactBaseForm):
    show_blocks = [
        'certificates',
        'constituency',
        'constituency_network',  # FIXME: correct?
        'contact_information',
        'csp_team',
        'memberships',
        'phone_numbers',
        'security_notification_scope',
        'team',
        'team_email_address',
        'team_members',
    ]

    class Meta(TeamContactBaseForm.Meta):
        fields = (
            'csp_id', 'csp_domain', 'csp_installed', 'nis_team_types',
            'nis_sectors', 'status',
            'short_name', 'name', 'host_organisation', 'country',
            'additional_countries', 'established', 'description',
            'constituency_types', 'constituency_description', 'member_locations',
            'constituency_asns', 'constituency_domains', 'constituency_ipranges',
            'scope_asns', 'scope_ipranges', 'scope_email',
            'contact_postal_address', 'contact_postal_country',
            'main_email', 'public_email', 'automated_email',
            'automated_email_format',
            'public_www', 'public_ftp', 'public_mailinglist', 'public_usenet',
            'business_hours', 'outside_business_hours', 'business_hours_timezone',
        )


class TeamContactFullForm(TeamContactTITUSForm):
    show_blocks = TeamContactTITUSForm.show_blocks + [
        'public_information_resources', 'business_hours',
        'billing_information', 'policies',
        'rfc2350', 'services_provided',
        'tools_and_expertise', 'process_information',
        'staff_information', 'csirt_support', 'irt_object',
        'outband_alerting', 'outband_alerting_contacts',
        'outband_alerting_accesses']


class TeamPhoneNumberForm(AnnotatedModelForm):
    class Meta:
        model = TeamPhoneNumber
        fields = '__all__'

        # All widgets definitions for phone forms go here
        widgets = {
            'tag': Suggest(source='phone_tag'),
            'number': forms.TextInput,
            'timezone': forms.TextInput,
            'number_details': forms.TextInput,
            'visibility': Suggest(source='phone_visibility')
        }


class TeamMembershipForm(AnnotatedModelForm):
    class Meta:
        model = TeamMembership
        fields = '__all__'

        widgets = {
            'membership_state': Suggest(source='membership_state'),
        }


class TeamCertificateForm(AnnotatedModelForm):
    class Meta:
        model = TeamKeyOrCertificate
        fields = '__all__'

        # All widgets definitions for certificate forms go here
        widgets = {
            'tag': Suggest(source='key_tag'),
            'method': Suggest(source='key_method'),
            'visibility': Suggest(source='key_visibility'),
            'keyid': forms.TextInput,
            'data': forms.TextInput,
        }


class OutbandAlertingContactForm(AnnotatedModelForm):
    class Meta:
        model = OutbandAlertingContact
        fields = '__all__'


class OutbandAlertingAccessForm(AnnotatedModelForm):
    class Meta:
        model = OutbandAlertingAccess
        fields = '__all__'


TeamPhoneNumberFormSet = inlineformset_factory(
    TeamContact,
    TeamPhoneNumber,
    can_delete=True,
    form=TeamPhoneNumberForm,
    fk_name='teamcontact',
    extra=0,
    exclude=['teamcontact'])


TeamCertificateFormSet = inlineformset_factory(
    TeamContact,
    TeamKeyOrCertificate,
    can_delete=True,
    form=TeamCertificateForm,
    fk_name='teamcontact',
    extra=0,
    exclude=['teamcontact'])


TeamMembershipFormSet = inlineformset_factory(
    TeamContact,
    TeamMembership,
    can_delete=True,
    form=TeamMembershipForm,
    fk_name='teamcontact',
    extra=0,
    exclude=['teamcontact'])


TeamOutbandAlertingContactFormSet = inlineformset_factory(
    TeamContact,
    OutbandAlertingContact,
    can_delete=True,
    form=OutbandAlertingContactForm,
    fk_name='teamcontact',
    extra=0,
    exclude=['teamcontact'])


TeamOutbandAlertingAccessFormSet = inlineformset_factory(
    TeamContact,
    OutbandAlertingAccess,
    can_delete=True,
    form=OutbandAlertingAccessForm,
    fk_name='teamcontact',
    extra=0,
    exclude=['teamcontact'])


class TeamMemberForm(AnnotatedModelForm):
    class Meta:
        model = TeamMember
        fields = '__all__'

        widgets = {
            'id': forms.HiddenInput,
            'full_name': forms.TextInput,
            'email': forms.TextInput,
            'email_visibility': Suggest(source='email_visibility'),
            'postal_country': Suggest(source='country'),
            'ml_email': forms.TextInput,
            'ml_key': forms.TextInput,
            'team_role': Suggest(source='team_role'),
        }


class TeamMemberPhoneNumberForm(AnnotatedModelForm):
    class Meta:
        model = TeamMemberPhoneNumber
        fields = '__all__'

        widgets = TeamPhoneNumberForm.Meta.widgets


class TeamMemberKeyOrCertificateForm(AnnotatedModelForm):
    class Meta:
        model = TeamMemberKeyOrCertificate
        fields = '__all__'

        widgets = TeamCertificateForm.Meta.widgets


class TeamMemberMembershipForm(AnnotatedModelForm):
    class Meta:
        model = TeamMemberMembership
        fields = '__all__'

        widgets = {
            'membership_state': Suggest(source='membership_state'),
        }


class TeamSharingTargetsForm(AnnotatedForm):
    ctcs = forms.ModelMultipleChoiceField(queryset=None, required=False)
    ltcs = forms.ModelMultipleChoiceField(queryset=None, required=False)
    teams = forms.ModelMultipleChoiceField(queryset=None, required=False)

    def __init__(self, *args, **kwargs):
        super(TeamSharingTargetsForm, self).__init__(*args, **kwargs)
        self.fields['ctcs'].queryset = TrustCircle.objects.all()
        self.fields['ltcs'].queryset = LocalTrustCircle.objects.all()
        self.fields['teams'].queryset = Team.objects.all()

    def clean(self):
        super(TeamSharingTargetsForm, self).clean()
        ctcs = self.cleaned_data.get('ctcs')
        ltcs = self.cleaned_data.get('ltcs')

        if len(ctcs) + len(ltcs) < 1:
            self.add_error(
                'ctcs', 'At least one CTC or LTC has to be selected.')
            self.add_error(
                'ltcs', 'At least one CTC or LTC has to be selected.')


class NestedInlineFormset(BaseInlineFormSet):
    @property
    def empty_form(self):
        # Overwritten to enable nested formsets to use a different
        # prefix placeholder. Necessary for the formset javascript.
        form = self.form(
            auto_id=self.auto_id,
            prefix=self.add_prefix('__nestedprefix__'),
            empty_permitted=True,
            use_required_attribute=False,
            **self.get_form_kwargs(None)
        )
        self.add_fields(form, None)
        return form


TeamMemberPhoneNumberFormSet = inlineformset_factory(
            TeamMember,
            TeamMemberPhoneNumber,
            can_delete=True,
            form=TeamMemberPhoneNumberForm,
            formset=NestedInlineFormset,
            fk_name='teammember',
            extra=0)


TeamMemberKeyOrCertificateFormSet = inlineformset_factory(
            TeamMember,
            TeamMemberKeyOrCertificate,
            can_delete=True,
            form=TeamMemberKeyOrCertificateForm,
            formset=NestedInlineFormset,
            fk_name='teammember',
            extra=0)


TeamMemberMembershipFormSet = inlineformset_factory(
            TeamMember,
            TeamMemberMembership,
            can_delete=True,
            form=TeamMemberMembershipForm,
            formset=NestedInlineFormset,
            fk_name='teammember',
            extra=0)


class BaseTeamMembersFormset(BaseInlineFormSet):
    def add_fields(self, form, index):
        super(BaseTeamMembersFormset, self).add_fields(form, index)
        form.team_member_phone_number_formset = TeamMemberPhoneNumberFormSet(
            instance=form.instance,
            data=form.data if form.is_bound else None,
            files=form.files if form.is_bound else None,
            prefix='{}-{}'.format(
                form.prefix,
                TeamMemberPhoneNumberFormSet.get_default_prefix()))

        form.team_member_certificate_formset = \
            TeamMemberKeyOrCertificateFormSet(
                instance=form.instance,
                data=form.data if form.is_bound else None,
                files=form.files if form.is_bound else None,
                prefix='{}-{}'.format(
                    form.prefix,
                    TeamMemberKeyOrCertificateFormSet.get_default_prefix()))

        form.team_member_membership_formset = TeamMemberMembershipFormSet(
            instance=form.instance,
            data=form.data if form.is_bound else None,
            files=form.files if form.is_bound else None,
            prefix='{}-{}'.format(
                form.prefix,
                TeamMemberMembershipFormSet.get_default_prefix()))

    def is_valid(self):
        result = super(BaseTeamMembersFormset, self).is_valid()

        values = set()
        for form in self.forms:
            if 'email' in form.cleaned_data and not (
                    'DELETE' in form.cleaned_data and
                    form.cleaned_data['DELETE']):
                value = form.cleaned_data['email']
                if value in values:
                    form.add_error(
                        'email',
                        'Each Team Member needs a unique email address.')
                    result = False
                values.add(value)

        if self.is_bound:
            for form in self.forms:
                if hasattr(form, 'team_member_phone_number_formset'):
                    result = (result and
                              form.team_member_phone_number_formset.is_valid())
                if hasattr(form, 'team_member_certificate_formset'):
                    result = (result and
                              form.team_member_certificate_formset.is_valid())
                if hasattr(form, 'team_member_membership_formset'):
                    result = (result and
                              form.team_member_membership_formset.is_valid())

        return result

    def save(self, commit=True):
        result = super(BaseTeamMembersFormset, self).save(commit=commit)

        for form in self.forms:
            if hasattr(form, 'team_member_phone_number_formset'):
                if not self._should_delete_form(form):
                    form.team_member_phone_number_formset.save(commit=commit)
            if hasattr(form, 'team_member_certificate_formset'):
                if not self._should_delete_form(form):
                    form.team_member_certificate_formset.save(commit=commit)
            if hasattr(form, 'team_member_membership_formset'):
                if not self._should_delete_form(form):
                    form.team_member_membership_formset.save(commit=commit)

        return result


TeamMemberFormSet = inlineformset_factory(
    TeamContact,
    TeamMember,
    formset=BaseTeamMembersFormset,
    can_delete=True,
    form=TeamMemberForm,
    fk_name='team',
    extra=0,
    exclude=['team', 'id'])


# Person
class PersonContactForm(AnnotatedModelForm):
    class Meta:
        model = PersonContact
        fields = '__all__'
        # Fields that should be single line are TextInput.
        # Most fields are saved as TextField in DB (max_length=infinity).
        widgets = {
            'full_name': forms.TextInput,
            'email': forms.TextInput,
            'email_visibility': Suggest(source='email_visibility'),
            'postal_country': Suggest(source='country'),
            'ml_email': forms.TextInput,
            'ml_key': forms.TextInput,
        }


class PersonPhoneNumberForm(AnnotatedModelForm):
    class Meta:
        model = PersonPhoneNumber
        fields = '__all__'

        widgets = TeamPhoneNumberForm.Meta.widgets


class PersonCertificateForm(AnnotatedModelForm):
    class Meta:
        model = PersonKeyOrCertificate
        fields = '__all__'

        widgets = TeamCertificateForm.Meta.widgets


class PersonMembershipForm(AnnotatedModelForm):
    class Meta:
        model = PersonMembership
        fields = '__all__'

        widgets = {
            'membership_state': Suggest(source='membership_state'),
        }


PersonPhoneNumberFormSet = inlineformset_factory(
    PersonContact,
    PersonPhoneNumber,
    can_delete=True,
    form=PersonPhoneNumberForm,
    fk_name='personcontact',
    extra=0,
    exclude=['personcontact'])

PersonCertificateFormSet = inlineformset_factory(
    PersonContact,
    PersonKeyOrCertificate,
    can_delete=True,
    form=PersonCertificateForm,
    fk_name='personcontact',
    extra=0,
    exclude=['personcontact'])

PersonMembershipFormSet = inlineformset_factory(
    PersonContact,
    PersonMembership,
    can_delete=True,
    form=PersonMembershipForm,
    fk_name='personcontact',
    extra=0,
    exclude=['personcontact'])


class LocalTrustCircleForm(AnnotatedModelForm):
    class Meta:
        model = LocalTrustCircle
        exclude = ['created']
        widgets = {
            'description': forms.widgets.Textarea(attrs={'rows': 5, 'cols': 40}),
            'trustcircles': forms.widgets.SelectMultiple(attrs={'size': '12'}),
            'teams': forms.widgets.SelectMultiple(attrs={'size': '12'}),
            'team_contacts': forms.widgets.SelectMultiple(attrs={'size': '12'}),
            'person_contacts': forms.widgets.SelectMultiple(attrs={'size': '12'}),
        }

