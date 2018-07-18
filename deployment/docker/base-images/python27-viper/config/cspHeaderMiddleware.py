from django.contrib.auth.middleware import RemoteUserMiddleware

class CspHeaderMiddleware(RemoteUserMiddleware):
    header = 'Custom-User-Id'