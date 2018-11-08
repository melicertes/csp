#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "csp.settings")

    # add ./lib to path -- #FIXME: just a hack for easy deployment during alpha phase
    sys.path.append(os.path.join(os.path.dirname(os.path.realpath(__file__)), 'lib'))

    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)
