from __future__ import absolute_import

from django.contrib import admin

from .models import ChangeLog


class ChangeLogAdmin(admin.ModelAdmin):
    list_display = ('id', 'created', 'action', 'model_name', 'model_pk',
                    'to_share', 'target_trustcircles')
    search_fields = ('action', 'model_pk', 'target_trustcircles')
    list_filter = ('action', 'model_name', 'target_trustcircles')


admin.site.register(ChangeLog, ChangeLogAdmin)
