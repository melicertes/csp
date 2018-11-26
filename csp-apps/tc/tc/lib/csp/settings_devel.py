import os

os.environ['IL_LOCAL_DOMAIN'] = 'localhost'
os.environ['CSP_NAME'] = 'devel-csp'

from .settings import *  # noqa

DEBUG = True

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql_psycopg2',
        'NAME': 'tcdb',
    }
}

LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'handlers': {
        'console': {
            'level': 'INFO',
            'class': 'logging.StreamHandler',
        },
    }
}

CSP_DSL_API_URL = 'http://localhost:8000/api/v1/adapter/integrationData'
