# -*- coding: utf-8 -*-
# Generated by Django 1.11.9 on 2019-09-12 09:53
from __future__ import unicode_literals

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('contacts', '0017_teamcontact_csp_team_link'),
    ]

    operations = [
        migrations.AlterField(
            model_name='teamcontact',
            name='country',
            field=models.TextField(blank=True),
        ),
        migrations.AlterField(
            model_name='teamcontact',
            name='established',
            field=models.DateField(blank=True, default=datetime.date.today, verbose_name='Established on'),
        ),
        migrations.AlterField(
            model_name='teamcontact',
            name='host_organisation',
            field=models.TextField(blank=True, default='', verbose_name='Host Organisation'),
        ),
        migrations.AlterField(
            model_name='teamcontact',
            name='name',
            field=models.TextField(default='', verbose_name='Official Name'),
        ),
        migrations.AlterField(
            model_name='teamcontact',
            name='short_name',
            field=models.TextField(verbose_name='Short Name'),
        ),
        migrations.AlterField(
            model_name='teamcontact',
            name='status',
            field=models.CharField(blank=True, default='', max_length=255),
        ),
    ]
