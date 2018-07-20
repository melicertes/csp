from django.contrib.auth.middleware import RemoteUserMiddleware

class CspHeaderMiddleware(RemoteUserMiddleware):
    header = 'HTTP_CUSTOM_USER_ID'