from uuid import UUID
from django import template
from csp.contacts.models import IncomingTeamContact
from csp.central.models import Team

register = template.Library()


@register.simple_tag
def unseen_contacts_count():
    return IncomingTeamContact.objects.filter(seen=None).count()


@register.filter
def uuid_resolve(uuids):
    if type(uuids) in [unicode, str]:
        try:
            ids = uuids.split(", ")
        except:
            pass
    else:
        ids = uuids

    if type(ids) != list:
        ids = [ids]

    ids = uuids
    if type(uuids) != list:
        ids = [uuids]
    if type(uuids) == unicode:
        try:
            ids = uuids.split(", ")
        except:
            pass

    resolved = []
    for uuid in ids:
        try:
            UUID(uuid, version=4)
        except ValueError:
            # If it's a value error, then the string
            # is not a valid hex code for a UUID.
            resolved.append(uuid)
            continue

        if Team.objects.filter(id=uuid).exists():
            team = Team.objects.filter(id=uuid)[0]
            resolved.append(team)
        else:
            resolved.append(uuid)

    #if type(uuids) != list and len(resolved) == 1:
    #    return resolved[0]
    return resolved
