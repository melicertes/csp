from __future__ import absolute_import

from django.contrib import admin

from .models import Suggestion, Country, ConfigKV


class SuggestionAdmin(admin.ModelAdmin):
    list_display = ('name', 'value')
    list_filter = ('name',)
    search_fields = ('name', 'value')


class CountryAdmin(admin.ModelAdmin):
    list_display = ('id', 'name')
    search_fields = ('id', 'name')


admin.site.register(Suggestion, SuggestionAdmin)
admin.site.register(Country, CountryAdmin)
admin.site.register(ConfigKV, admin.ModelAdmin)
