from django.contrib.auth.middleware import RemoteUserMiddleware
from django.conf import settings


class OAMRemoteUserMiddleware(RemoteUserMiddleware):
    header = 'HTTP_CUSTOM_USER_ID'


class OAMPermissionsMiddleware(object):
    header = 'HTTP_CUSTOM_USER_IS_MEMBER_OF'

    def process_request(self, request):
        if not request.user.is_authenticated():
            return

        oam_group_perms = getattr(settings, 'OPENAM_GROUP_PERMISSIONS', {})
        if not oam_group_perms:
            return

        oam_groups = [g.strip() for g in request.META.get(self.header, '').split('|')]
        oam_perms = set()
        for group in oam_groups:
            oam_perms.update(oam_group_perms.get(group, []))

        request.user.oam_permissions = oam_perms
