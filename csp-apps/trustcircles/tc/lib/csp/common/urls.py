from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.search_view, name='index'),
    url(r'^search/', views.search_view, name='search_view'),
]
