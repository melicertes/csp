# -*- coding: utf-8 -*-
# Generated by Django 1.11.9 on 2019-07-31 13:47
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('central', '0005_auto_20180213_1310'),
    ]

    operations = [
        migrations.AlterField(
            model_name='historicalteam',
            name='description',
            field=models.TextField(verbose_name='Description'),
        ),
        migrations.AlterField(
            model_name='team',
            name='description',
            field=models.TextField(verbose_name='Description'),
        ),
    ]
