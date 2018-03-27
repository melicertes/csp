"""
Django settings for CSP project.
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os

from csp.global_user import add_csp_info_to_record

BASE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), '../../')

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '&kya6a!v3wos9z-z)y-8@x47_!t%gryh!kx0+8un%(pe6d*ucg'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = False

ALLOWED_HOSTS = ['*']

# Database
# https://docs.djangoproject.com/en/1.8/ref/settings/#databases

# Uncomment this block and comment out the next if you want to use a PostgreSQL
# database instead of the default SQlite.

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql_psycopg2',
        'NAME': 'tcdb',
        'USER': 'postgres',
        'PASSWORD': 'postgres',
        'HOST': 'csp-postgres',
        'PORT': 5432,
    }
}

# Default DSL-API configuration if no arguments are provided for dsl_sync command
CSP_DSL_API_URL = 'https://' + os.environ['IL_LOCAL_DOMAIN'] + '/v1/dsl/integrationData'
CSP_DSL_CERT_FILE = '/opt/ssl/server/csp-internal.crt'
CSP_DSL_KEY_FILE = '/opt/ssl/server/csp-internal.key'
CSP_DSL_CACHAIN_FILE = '/opt/ssl/ca/common-internal-ca.crt'
CSP_ID = os.environ['CSP_NAME']
CSP_APP_ID = 'trustCircle'


OPENAM_GROUP_PERMISSIONS = {
    'cn=csp-user,ou=groups,dc=openam,dc=forgerock,dc=org': [
        'ctc.api_read',
        'ctc.web_read',
    ],
    'cn=tc-admin,ou=groups,dc=openam,dc=forgerock,dc=org': [
        'ctc.api_write',
        'ctc.web_write',
    ]
}

LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'filters': {
        'require_debug_false': {
            '()': 'django.utils.log.RequireDebugFalse',
        },
        'require_debug_true': {
            '()': 'django.utils.log.RequireDebugTrue',
        },
        'add_username': {
            '()': 'django.utils.log.CallbackFilter',
            'callback': add_csp_info_to_record,
        },
    },
    'formatters': {
        'csp': {
            'format': "%(asctime)s %(hostname)s CSP.TC[%(process)s]: %(username)s, %(message)s",
            'datefmt': '%b %d %H:%M:%S',
        },
    },
    'handlers': {
        'console': {
            'level': 'WARNING',
            'filters': ['require_debug_true'],
            'class': 'logging.StreamHandler',
        },
        'null': {
            'class': 'logging.NullHandler',
        },
        'mail_admins': {
            'level': 'ERROR',
            'filters': ['require_debug_false'],
            'class': 'django.utils.log.AdminEmailHandler'
        },
        'audit': {
            'level': 'INFO',
            'class': 'logging.FileHandler',
            'filename': '/opt/csplogs/trustcircles-aud.log',
            'filters': ['add_username'],
            'formatter': 'csp',
        },
        'exceptions': {
            'level': 'INFO',
            'class': 'logging.FileHandler',
            'filename': '/opt/csplogs/trustcircles-exc.log',
            'filters': ['add_username'],
            'formatter': 'csp',
        }
    },
    'loggers': {
        '': {
            'handlers': ['console', 'exceptions'],
        },
        'ctc': {
            'handlers': ['audit'],
            'level': 'INFO',
            'propagate': False,
        },
        'django.request': {
            'handlers': ['mail_admins'],
            'level': 'ERROR',
            'propagate': True,
        },
        'django.security': {
            'handlers': ['mail_admins'],
            'level': 'ERROR',
            'propagate': True,
        },
        'py.warnings': {
            'handlers': ['console'],
        },
    }
}

LOGIN_URL = "login_view"

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'django.forms',
    'simple_history',
    'django_filters',
    'rest_framework',
    'rest_framework_swagger',
    'corsheaders',
    'csp.common',
    'csp.central',
    'csp.contacts',
    'csp.integration',
    'csp.openam_auth',
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'corsheaders.middleware.CorsMiddleware',
    'simple_history.middleware.HistoryRequestMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'csp.openam_auth.middleware.OAMRemoteUserMiddleware',
    'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'csp.openam_auth.middleware.OAMPermissionsMiddleware',
    'csp.global_user.GlobalUserMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'django.middleware.security.SecurityMiddleware',
)

AUTHENTICATION_BACKENDS = [
    'django.contrib.auth.backends.ModelBackend',
    'django.contrib.auth.backends.RemoteUserBackend',
]

AUTH_USER_MODEL = 'openam_auth.User'

ROOT_URLCONF = 'csp.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [
            os.path.join(BASE_DIR, 'lib/csp/templates')
        ],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

FORM_RENDERER = 'django.forms.renderers.TemplatesSetting'

WSGI_APPLICATION = 'csp.wsgi.application'

# Internationalization
# https://docs.djangoproject.com/en/1.8/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.8/howto/static-files/

STATIC_URL = '/static/'
STATIC_ROOT = os.path.join(BASE_DIR, 'var/static')

STATICFILES_DIRS = [
    os.path.join(BASE_DIR, 'lib/csp/static'),
]

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': (
        'rest_framework.authentication.BasicAuthentication',
        'rest_framework.authentication.SessionAuthentication',
    )
}

CORS_ORIGIN_ALLOW_ALL = True
CORS_URLS_REGEX = r'^/api/.*$'

try:
    from .settings_local import *  # noqa
except:  # noqa
    pass
