# -*- coding: utf-8 -*-
# Generated by Django 1.11.9 on 2018-02-02 10:41
from __future__ import unicode_literals

import csp.central.models
import django.contrib.postgres.fields
from django.db import migrations, models
import django.utils.timezone
import uuid


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='HistoricalTeam',
            fields=[
                ('id', models.UUIDField(db_index=True, default=uuid.uuid4)),
                ('short_name', models.CharField(max_length=128, verbose_name='(Short) Team Name')),
                ('name', models.CharField(max_length=255, verbose_name='(Long) Team Name')),
                ('host_organisation', models.CharField(max_length=255, verbose_name='Host Organization')),
                ('description', models.CharField(max_length=255, verbose_name='Description')),
                ('country', models.CharField(max_length=255)),
                ('additional_countries', django.contrib.postgres.fields.ArrayField(base_field=models.TextField(), blank=True, default=list, size=None)),
                ('established', models.DateField(verbose_name='Established on')),
                ('nis_team_types', django.contrib.postgres.fields.ArrayField(base_field=models.TextField(), blank=True, default=list, size=None)),
                ('nis_sectors', django.contrib.postgres.fields.ArrayField(base_field=models.TextField(), blank=True, default=list, size=None)),
                ('created', models.DateTimeField(default=django.utils.timezone.now, verbose_name='Created on')),
                ('csp_installed', models.BooleanField(default=False, verbose_name='CSP Installed')),
                ('csp_id', models.CharField(blank=True, max_length=255, verbose_name='CSP ID')),
                ('csp_domain', models.CharField(blank=True, max_length=255, verbose_name='CSP Domain')),
                ('status', models.CharField(max_length=255)),
                ('history_id', models.AutoField(primary_key=True, serialize=False)),
                ('history_date', models.DateTimeField()),
                ('history_type', models.CharField(choices=[('+', 'Created'), ('~', 'Changed'), ('-', 'Deleted')], max_length=1)),
            ],
            options={
                'ordering': ('-history_date', '-history_id'),
                'get_latest_by': 'history_date',
                'verbose_name': 'historical team',
            },
        ),
        migrations.CreateModel(
            name='HistoricalTrustCircle',
            fields=[
                ('id', models.UUIDField(db_index=True, default=uuid.uuid4)),
                ('short_name', models.CharField(db_index=True, help_text='Needs to start with "CTC::"', max_length=128, validators=[csp.central.models.ctc_prefix_validator], verbose_name='(Short) TC Name')),
                ('name', models.CharField(max_length=255, verbose_name='(Long) TC Name')),
                ('description', models.CharField(max_length=255, verbose_name='Description')),
                ('auth_source', models.CharField(default='ENISA', max_length=255, verbose_name='Authoritative Source')),
                ('info_url', models.URLField(blank=True, verbose_name='URL for Public Information')),
                ('membership_url', models.URLField(blank=True, verbose_name='URL for Membership Directory')),
                ('created', models.DateTimeField(default=django.utils.timezone.now, verbose_name='Created on')),
                ('history_id', models.AutoField(primary_key=True, serialize=False)),
                ('history_date', models.DateTimeField()),
                ('history_type', models.CharField(choices=[('+', 'Created'), ('~', 'Changed'), ('-', 'Deleted')], max_length=1)),
            ],
            options={
                'ordering': ('-history_date', '-history_id'),
                'get_latest_by': 'history_date',
                'verbose_name': 'historical trust circle',
            },
        ),
        migrations.CreateModel(
            name='Team',
            fields=[
                ('id', models.UUIDField(default=uuid.uuid4, primary_key=True, serialize=False)),
                ('short_name', models.CharField(max_length=128, verbose_name='(Short) Team Name')),
                ('name', models.CharField(max_length=255, verbose_name='(Long) Team Name')),
                ('host_organisation', models.CharField(max_length=255, verbose_name='Host Organization')),
                ('description', models.CharField(max_length=255, verbose_name='Description')),
                ('country', models.CharField(max_length=255)),
                ('additional_countries', django.contrib.postgres.fields.ArrayField(base_field=models.TextField(), blank=True, default=list, size=None)),
                ('established', models.DateField(verbose_name='Established on')),
                ('nis_team_types', django.contrib.postgres.fields.ArrayField(base_field=models.TextField(), blank=True, default=list, size=None)),
                ('nis_sectors', django.contrib.postgres.fields.ArrayField(base_field=models.TextField(), blank=True, default=list, size=None)),
                ('created', models.DateTimeField(default=django.utils.timezone.now, verbose_name='Created on')),
                ('csp_installed', models.BooleanField(default=False, verbose_name='CSP Installed')),
                ('csp_id', models.CharField(blank=True, max_length=255, verbose_name='CSP ID')),
                ('csp_domain', models.CharField(blank=True, max_length=255, verbose_name='CSP Domain')),
                ('status', models.CharField(max_length=255)),
            ],
            options={
                'ordering': ['short_name'],
                'permissions': (('api_read', 'Read-Only access to REST-API'), ('api_write', 'Read-Write access to REST-API'), ('web_write', 'Read-Only access to Web Frontend')),
            },
        ),
        migrations.CreateModel(
            name='TrustCircle',
            fields=[
                ('id', models.UUIDField(default=uuid.uuid4, primary_key=True, serialize=False)),
                ('short_name', models.CharField(help_text='Needs to start with "CTC::"', max_length=128, unique=True, validators=[csp.central.models.ctc_prefix_validator], verbose_name='(Short) TC Name')),
                ('name', models.CharField(max_length=255, verbose_name='(Long) TC Name')),
                ('description', models.CharField(max_length=255, verbose_name='Description')),
                ('auth_source', models.CharField(default='ENISA', max_length=255, verbose_name='Authoritative Source')),
                ('info_url', models.URLField(blank=True, verbose_name='URL for Public Information')),
                ('membership_url', models.URLField(blank=True, verbose_name='URL for Membership Directory')),
                ('created', models.DateTimeField(default=django.utils.timezone.now, verbose_name='Created on')),
                ('teams', models.ManyToManyField(blank=True, related_name='circles', to='central.Team', verbose_name='Member Teams of the Trust Circle')),
            ],
        ),
        migrations.AlterUniqueTogether(
            name='team',
            unique_together=set([('country', 'short_name')]),
        ),
    ]
