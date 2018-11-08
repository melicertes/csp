from __future__ import absolute_import

from django.conf.urls import url

from rest_framework.documentation import include_docs_urls
from rest_framework_swagger.views import get_swagger_view

from . import api

title = 'CSP API Documentation'
schema_view = get_swagger_view(title=title)

urlpatterns = [
    url(r'^docs1/', schema_view),
    url(r'^docs2/', include_docs_urls(title=title)),

    url(r'^teams$',
        api.TeamViewSet.as_view({'get': 'list', 'post': 'create'}),
        name='teams-api'),

    url(r'^teams/(?P<pk>[0-9a-z-]+)$',
        api.TeamViewSet.as_view({'get': 'retrieve', 'put': 'update'}),
        name='team-api'),

    url(r'^circles$',
        api.TrustCircleViewSet.as_view({'get': 'list', 'post': 'create'}),
        name='circles-api'),

    url(r'^circles/(?P<pk>.+)$',
        api.TrustCircleViewSet.as_view({'get': 'retrieve',
                                        'put': 'update',
                                        'delete': 'destroy'}),
        name='circle-api'),
]
