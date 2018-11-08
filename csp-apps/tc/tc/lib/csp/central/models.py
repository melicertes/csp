from __future__ import unicode_literals

import uuid

from django.contrib.postgres.fields import ArrayField, JSONField
from django.core.exceptions import ValidationError
from django.db import models
from django.utils import timezone

from simple_history.models import HistoricalRecords


class Team(models.Model):
    id = models.UUIDField(default=uuid.uuid4, primary_key=True)
    short_name = models.CharField('(Short) Team Name', max_length=128)
    name = models.CharField('(Long) Team Name', max_length=255)
    host_organisation = models.CharField('Host Organization', max_length=255)
    description = models.CharField('Description', max_length=255)
    country = models.CharField(max_length=255)
    additional_countries = ArrayField(models.TextField(), default=list, blank=True)
    established = models.DateField('Established on')
    nis_team_types = ArrayField(models.TextField(), default=list, blank=True,
                                verbose_name='NIS Team Types')
    nis_sectors = ArrayField(models.TextField(), default=list, blank=True,
                             verbose_name='NIS Sectors')
    created = models.DateTimeField('Created on', auto_now_add=True)
    csp_installed = models.BooleanField('CSP Installed', default=False)
    csp_id = models.CharField('CSP ID', max_length=255, blank=True)
    csp_domain = models.CharField('CSP Domain', max_length=255, blank=True)
    status = models.CharField(max_length=255)

    history = HistoricalRecords()

    team_selfinfo = JSONField(default=None, blank=True, null=True)

    class Meta:
        unique_together = ('country', 'short_name')
        ordering = ['short_name']
        permissions = (
            ('api_read', 'Read-Only access to REST-API'),
            ('api_write', 'Read-Write access to REST-API'),
            ('web_write', 'Read-Only access to Web Frontend'),
        )

    def __unicode__(self):
        return '%s, %s' % (self.short_name, self.country)


def ctc_prefix_validator(value):
    if not value.startswith('CTC::'):
        raise ValidationError('Trust circle short names need to start with "CTC::"')


TLP_CHOICES = (
    ('red', 'Red'),
    ('amber', 'Amber'),
    ('green', 'Green'),
    ('white', 'White'),
)


class TrustCircle(models.Model):
    id = models.UUIDField(default=uuid.uuid4, primary_key=True)
    short_name = models.CharField('(Short) TC Name', max_length=128, unique=True,
                                  validators=[ctc_prefix_validator],
                                  help_text='Needs to start with "CTC::"')
    tlp = models.CharField('TLP', max_length=255, choices=TLP_CHOICES, blank=True, default='')
    name = models.CharField('(Long) TC Name', max_length=255)
    description = models.CharField('Description', max_length=255)
    auth_source = models.CharField('Authoritative Source', max_length=255, default='ENISA')
    info_url = models.URLField('URL for Public Information', blank=True)
    membership_url = models.URLField('URL for Membership Directory', blank=True)
    created = models.DateTimeField('Created on', default=timezone.now)
    history = HistoricalRecords()
    teams = models.ManyToManyField(Team, blank=True,
                                   verbose_name='Member Teams of the Trust Circle',
                                   related_name='circles')

    def __unicode__(self):
        return self.name
