from django.contrib import admin
from .models import (
    TeamContact,
    PersonContact,
    TeamPhoneNumber,
    PersonPhoneNumber,
    TeamKeyOrCertificate,
    PersonKeyOrCertificate,
    TeamMembership,
    PersonMembership,
    OutbandAlertingContact,
    OutbandAlertingAccess,
    LocalTrustCircle,
    IncomingTeamContact)


admin.site.register(TeamContact)
admin.site.register(PersonContact)
admin.site.register(TeamPhoneNumber)
admin.site.register(TeamKeyOrCertificate)
admin.site.register(TeamMembership)
admin.site.register(PersonPhoneNumber)
admin.site.register(PersonKeyOrCertificate)
admin.site.register(PersonMembership)
admin.site.register(OutbandAlertingContact)
admin.site.register(OutbandAlertingAccess)
admin.site.register(LocalTrustCircle)
admin.site.register(IncomingTeamContact)
