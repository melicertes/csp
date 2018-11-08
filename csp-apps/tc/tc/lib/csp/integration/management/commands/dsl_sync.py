from __future__ import unicode_literals

import logging
import time

from django.conf import settings
from django.core.management.base import BaseCommand, CommandError
from django.db import transaction

from csp.integration.models import ChangeLog
from csp.integration.dsl import DSL

LOG = logging.getLogger('dsl_sync')


class Command(BaseCommand):
    help = 'Synchronizes changes with the CSP integration layer'

    def add_arguments(self, parser):
        parser.add_argument('--url', help='URL of the dsl integration endpoint')
        parser.add_argument('--cert', help='PEM-encoded client certificate')
        parser.add_argument('--key', help='PEM-encoded client certificate key (can be the same as --cert)')
        parser.add_argument('--cachain', help='PEM-encoded certificate chain for server validation')
        parser.add_argument('--csp-id', help='ID of this CSP instance')
        parser.add_argument('--app-id', help='ID of this APP instance')
        group = parser.add_mutually_exclusive_group()
        group.add_argument('--interval', help='To run this command continuously with a delay of <INTERVAL> seconds',
                           metavar='INTERVAL', type=int)
        group.add_argument('--clear', help='Clear all pending synchronization jobs',
                           action='store_true')

    def handle(self, url=None, cert=None, key=None, cachain=None, csp_id=None,
               app_id=None, interval=None, clear=False, **kwargs):
        if clear:
            num = clear_pending_changes()
            if num:
                self.stdout.write('Cleared %d pending change%s\n'
                                  % (num, '' if num == 1 else 's'))
            else:
                self.stdout.write('No pending changes found\n')
            return

        if csp_id is None:
            csp_id = settings.CSP_ID
        if app_id is None:
            app_id = settings.CSP_APP_ID

        dsl = DSL(url or settings.CSP_DSL_API_URL,
                  cert or settings.CSP_DSL_CERT_FILE,
                  key or settings.CSP_DSL_KEY_FILE,
                  cachain or settings.CSP_DSL_CACHAIN_FILE,
                  csp_id, app_id)

        while True:
            try:
                dispatch_changes(dsl)
            except Exception as e:
                LOG.exception('DSL synchronization error')
                if not interval:
                    raise CommandError(e.message)

            if interval:
                time.sleep(interval)
            else:
                break


def dispatch_changes(dsl):
    while True:
        with transaction.atomic():
            change = (ChangeLog.objects
                      .select_for_update()
                      .order_by('created')
                      .first())
            if change is None:
                break
            dsl.dispatch_change(change)
            change.delete()


@transaction.atomic
def clear_pending_changes():
    entries = ChangeLog.objects.select_for_update().all()
    count = entries.count()
    entries.delete()
    return count
