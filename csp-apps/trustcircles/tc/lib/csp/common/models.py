from __future__ import unicode_literals

from django.db import models
from django.db.models import Func, F
from django.apps import apps


class ConfigKV(models.Model):
    CONFIG_CHOICES = (
        ('remove_incoming_after_days',
            'How many days to keep incoming contacts'),
    )

    key = models.CharField(max_length=255, db_index=True,
                           unique=True, choices=CONFIG_CHOICES)
    value = models.TextField()

    def __unicode__(self):
        return '{}:{}'.format(self.key, self.value)


class Country(models.Model):
    id = models.CharField(max_length=3, primary_key=True)
    name = models.CharField(max_length=256)

    class Meta:
        verbose_name = 'Country'
        verbose_name_plural = 'Countries'

    def __unicode__(self):
        return '{}:{}'.format(self.id, self.name)


class Suggestion(models.Model):
    NAME_CHOICES = (
        ('constituency_type', 'Constituency Type'),
        ('email_visibility', 'Email Visibility'),
        ('membership_state', 'Membership Status'),
        ('nis_sector', 'NIS Sector'),
        ('nis_team_type', 'NIS Team Type'),
        ('proactive_service', 'Proactive Service'),
        ('quality_management', 'Quality Management'),
        ('reactive_service', 'Reactive Service'),
        ('team_role', 'Team Role'),
        ('team_status', 'Team Status'),
        ('phone_tag', 'Phone Tag'),
        ('phone_visibility', 'Phone Visibility'),
        ('key_tag', 'Key Tag'),
        ('key_method', 'Key Method'),
        ('key_visibility', 'Key Visibility'),
    )

    name = models.CharField(max_length=255, db_index=True, choices=NAME_CHOICES)
    value = models.CharField(max_length=10000)

    def __unicode__(self):
        return '{}:{}'.format(self.name, self.value)


SUGGESION_SOURCES = {
    'country': [
        {'model': 'central.team', 'field': 'country', 'type': 'single'},
        {'model': 'central.team', 'field': 'additional_countries', 'type': 'array'},
        {'model': 'contacts.teamcontact', 'field': 'country', 'type': 'single'},
        {'model': 'contacts.teamcontact', 'field': 'additional_countries', 'type': 'array'},
        {'model': 'contacts.teamcontact', 'field': 'contact_postal_country', 'type': 'single'},
        {'model': 'contacts.teamcontact', 'field': 'billing_postal_country', 'type': 'single'},
        {'model': 'contacts.teammember', 'field': 'postal_country', 'type': 'single'},
        {'model': 'contacts.personcontact', 'field': 'postal_country', 'type': 'single'},
    ],
    'constituency_type': [
        {'model': 'contacts.teamcontact', 'field': 'constituency_types', 'type': 'array'},
    ],
    'nis_sectors': [
        {'model': 'contacts.teamcontact', 'field': 'nis_sectors', 'type': 'array'},
        {'model': 'central.team', 'field': 'nis_sectors', 'type': 'array'},
    ],
    'nis_team_type': [
        {'model': 'contacts.teamcontact', 'field': 'nis_team_types', 'type': 'array'},
        {'model': 'central.team', 'field': 'nis_team_types', 'type': 'array'},
    ],
    'team_status': [
        {'model': 'contacts.teamcontact', 'field': 'status', 'type': 'single'},
        {'model': 'central.team', 'field': 'status', 'type': 'single'},
    ],
    'team_role': [
        {'model': 'contacts.teammember', 'field': 'team_role', 'type': 'single'},
    ],
    'reactive_service': [
        {'model': 'contacts.teamcontact', 'field': 'reactive_services', 'type': 'array'},
    ],
    'proactive_service': [
        {'model': 'contacts.teamcontact', 'field': 'proactive_services', 'type': 'array'},
    ],
    'quality_management': [
        {'model': 'contacts.teamcontact', 'field': 'quality_management', 'type': 'array'},
    ],
    'membership_state': [
        {'model': 'contacts.teammembership', 'field': 'membership_state', 'type': 'single'},
        {'model': 'contacts.personmembership', 'field': 'membership_state', 'type': 'single'},
        {'model': 'contacts.teammembermembership', 'field': 'membership_state', 'type': 'single'},
    ],
    'phone_tag': [
        {'model': 'contacts.teamphonenumber', 'field': 'tag', 'type': 'single'},
        {'model': 'contacts.personphonenumber', 'field': 'tag', 'type': 'single'},
        {'model': 'contacts.teammemberphonenumber', 'field': 'tag', 'type': 'single'},
    ],
    'phone_visibility': [
        {'model': 'contacts.teamphonenumber', 'field': 'visibility', 'type': 'single'},
        {'model': 'contacts.personphonenumber', 'field': 'visibility', 'type': 'single'},
        {'model': 'contacts.teammemberphonenumber', 'field': 'visibility', 'type': 'single'},
    ],
    'key_tag': [
        {'model': 'contacts.teamkeyorcertificate', 'field': 'tag', 'type': 'single'},
        {'model': 'contacts.personkeyorcertificate', 'field': 'tag', 'type': 'single'},
        {'model': 'contacts.teammemberkeyorcertificate', 'field': 'tag', 'type': 'single'},
    ],
    'key_visibility': [
        {'model': 'contacts.teamkeyorcertificate', 'field': 'visibility', 'type': 'single'},
        {'model': 'contacts.personkeyorcertificate', 'field': 'visibility', 'type': 'single'},
        {'model': 'contacts.teammemberkeyorcertificate', 'field': 'visibility', 'type': 'single'},
    ],
    'key_method': [
        {'model': 'contacts.teamkeyorcertificate', 'field': 'method', 'type': 'single'},
        {'model': 'contacts.personkeyorcertificate', 'field': 'method', 'type': 'single'},
        {'model': 'contacts.teammemberkeyorcertificate', 'field': 'method', 'type': 'single'},
    ],
}


def get_suggestions_for(name, search=None):
    """
    Searches for suggestions in the Suggestion or Country models and also in
    all columns that make use of these suggestions.
    """
    if name == 'country':
        base_qs = Country.objects.all()
        if search:
            base_qs = base_qs.filter(name__icontains=search)
        base_qs = base_qs.values(value=F('name'))
    else:
        base_qs = Suggestion.objects.filter(name=name)
        if search:
            base_qs = base_qs.filter(value__icontains=search)
        base_qs = base_qs.values('value')

    single_qss = []
    array_qss = []

    for entry in SUGGESION_SOURCES.get(name, []):
        model = apps.get_model(entry['model'])
        qs = model.objects.order_by()
        if search:
            qs = qs.filter(**{'{}__icontains'.format(entry['field']): search})

        if entry['type'] == 'array':
            array_qss.append(qs.values(value=Func(F(entry['field']), function='unnest')))
        else:
            single_qss.append(qs.values(value=F(entry['field'])))

    if single_qss:
        base_qs = base_qs.union(*single_qss)

    results = set(entry['value'] for entry in base_qs)

    if array_qss:
        array_qs = array_qss[0].union(*array_qss[1:])
        for row in array_qs:
            results.update(entry['value'] for entry in array_qs
                           if not search or search.lower() in entry['value'].lower())

    return sorted(results)
