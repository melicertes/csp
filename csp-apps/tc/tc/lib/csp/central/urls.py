from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^teams/$', views.team_list, name='team_list'),
    url(r'^teams/delete/$', views.team_delete, name='team_delete'),
    url(r'^teams/new/$', views.team_edit, name='team_new'),
    url(r'^teams/edit/(?P<id>[0-9a-z-]+)/$', views.team_edit, name='team_edit'),
    url(r'^teams/view/(?P<id>[0-9a-z-]+)/$', views.team_view, name='team_view'),
    url(r'^teams/history/(?P<id>[0-9a-z-]+)/$', views.team_history_view, name='team_history'),

    url(r'^circles/$', views.circle_list, name='circle_list'),
    url(r'^circles/delete/$', views.circle_delete, name='circle_delete'),
    url(r'^circles/new/$', views.circle_edit, name='circle_new'),
    url(r'^circles/edit/(?P<id>[0-9a-z-]+)/$', views.circle_edit, name='circle_edit'),
    url(r'^circles/view/(?P<id>[0-9a-z-]+)/$', views.circle_view, name='circle_view'),
    url(r'^circles/history/(?P<id>[0-9a-z-]+)/$', views.circle_history_view, name='circle_history'),
]
