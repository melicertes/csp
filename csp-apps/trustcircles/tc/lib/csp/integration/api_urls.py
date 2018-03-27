from __future__ import absolute_import

from django.conf.urls import url

from . import api

urlpatterns = [
    url(r'^adapter/integrationData$', api.AdapterView.as_view()),
]
