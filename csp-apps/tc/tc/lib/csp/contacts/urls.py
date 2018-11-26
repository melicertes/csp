from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^contacts/teams/$', views.teamcontact_list, name='teamcontact_list'),
    url(r'^contacts/teams/new/$', views.teamcontact_edit, name='teamcontact_new'),
    url(r'^contacts/teams/new/(?P<incoming_id>[0-9]+)/$', views.teamcontact_edit, name='teamcontact_new'),
    url(r'^contacts/teams/delete/$', views.teamcontact_delete, name='teamcontact_delete'),
    url(r'^contacts/teams/edit/(?P<id>[0-9a-z-]+)/$', views.teamcontact_edit, name='teamcontact_edit'),
    url(r'^contacts/teams/edit/(?P<id>[0-9a-z-]+)/(?P<incoming_id>[0-9]+)/$',
        views.teamcontact_edit, name='teamcontact_edit'),
    url(r'^contacts/teams/view/(?P<id>[0-9a-z-]+)/$', views.teamcontact_view, name='teamcontact_view'),
    url(r'^contacts/teams/share/(?P<id>[0-9a-z-]+)/$', views.teamcontact_share, name='teamcontact_share'),

    url(r'^contacts/people/$', views.personcontact_list, name='personcontact_list'),
    url(r'^contacts/people/new/$', views.personcontact_edit, name='personcontact_new'),
    url(r'^contacts/people/delete/$', views.personcontact_delete, name='personcontact_delete'),
    url(r'^contacts/people/edit/(?P<id>[0-9a-z-]+)/$', views.personcontact_edit, name='personcontact_edit'),
    url(r'^contacts/people/view/(?P<id>[0-9a-z-]+)/$', views.personcontact_view, name='personcontact_view'),

    url(r'^circles/$', views.ltc_list, name="ltc_list"),
    url(r'^circles/new/$', views.ltc_edit, name='ltc_new'),
    url(r'^circles/delete/$', views.ltc_delete, name='ltc_delete'),
    url(r'^circles/edit/(?P<id>[0-9a-z-]+)/$', views.ltc_edit, name='ltc_edit'),
    url(r'^circles/view/(?P<id>[0-9a-z-]+)/$', views.ltc_view, name='ltc_view'),

    url(r'^received/$', views.incoming_list, name="incomingcontact_list"),
    url(r'^received/delete/$', views.incoming_delete, name="incomingcontact_delete"),
    url(r'^received/view/(?P<id>[0-9]+)/$', views.incoming_view, name="incomingcontact_view"),
]
