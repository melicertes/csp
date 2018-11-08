"""
WSGI config for CSP project.

It exposes the WSGI callable as a module-level variable named ``application``.

For more information on this file, see
https://docs.djangoproject.com/en/1.8/howto/deployment/wsgi/
"""

import os
import sys

sys.path.append(
    os.path.join(os.path.dirname(os.path.abspath(__file__)), 'lib')
)

from django.core.wsgi import get_wsgi_application  # noqa

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "csp.settings")

application = get_wsgi_application()
