# -*- coding: utf-8 -*-
# Generated by Django 1.11.9 on 2019-08-07 15:44
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('contacts', '0014_auto_20190807_1519'),
    ]

    operations = [
        migrations.AlterField(
            model_name='localtrustcircle',
            name='created',
            field=models.DateTimeField(auto_now_add=True, verbose_name='Created on'),
        ),
    ]
