from __future__ import absolute_import

from django.conf.urls import url

from csp.integration.api import ExportView

from . import api

urlpatterns = [
    url(r'^suggest/(?P<name>.+)$', api.SuggestionView.as_view(), name='suggest_api'),
    url(r'^export/all$', ExportView.as_view(), name="export-to-all"),
]
