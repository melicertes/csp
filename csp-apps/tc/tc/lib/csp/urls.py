from django.conf.urls import include, url
from django.contrib import admin
from django.conf import settings


urlpatterns = [
    url(r'^admin/', include(admin.site.urls)),

    url(r'^api/v1/', include('csp.central.api_urls')),
    url(r'^api/v1/', include('csp.integration.api_urls')),
    url(r'^api/v1/', include('csp.contacts.api_urls')),
    url(r'^webapi/v1/', include('csp.common.api_urls')),

    url(r'^central/', include('csp.central.urls')),
    url(r'^local/', include('csp.contacts.urls')),

    url(r'^web/', include('csp.openam_auth.urls')),

    url(r'', include('csp.common.urls')),
]

if settings.DEBUG:
    from django.conf.urls.static import static
    urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
