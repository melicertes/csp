from __future__ import absolute_import

from rest_framework.routers import DefaultRouter

from . import api

router = DefaultRouter(trailing_slash=False)
router.register(r'ltc', api.LocalTrustCircleViewSet)
router.register(r'teamcontacts', api.TeamContactViewSet)
router.register(r'personcontacts', api.PersonContactViewSet)

# Temporary alias for teamcontacts, for backwards compatibility.
# FIXME: Remove when not necessary anymore!
router.register(r'contacts', api.TeamContactViewSet)

urlpatterns = router.urls
