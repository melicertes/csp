import socket
import threading

LOCALS = threading.local()


def get_current_username():
    return getattr(LOCALS, 'username', None)


def set_current_username(username):
    LOCALS.username = username


class GlobalUserMiddleware(object):
    def process_request(self, request):
        user = getattr(request, 'user', None)
        if user:
            set_current_username(user.username)


def add_csp_info_to_record(record):
    record.username = get_current_username() or 'system'
    record.hostname = socket.getaddrinfo(
        socket.gethostname(), 0, 0, 0, 0, socket.AI_CANONNAME)[0][3]
    return True
