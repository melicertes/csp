import os
from django import template

register = template.Library()


@register.simple_tag(takes_context=True)
def active_for(context, pattern):
    request = context.get('request')
    if request and request.path.startswith(pattern):
        return 'active'


@register.simple_tag(takes_context=True)
def is_official_deployment(context):
    request = context.get('request')
    if request:
        host = request.get_host()
    else:
        host = ''

    domain = os.getenv('DOMAIN')

    if (
        (host and ('melicertes.eu' in host or
                   'localhost' in host or
                   '127.0.0.1' in host)) or
        (domain and 'melicertes.eu' in domain)
      ):
        return True
    return False
