#!/bin/ash
 mkdir -p /opt/csplogs
 python2 manage.py migrate
 # python manage.py loaddata initial_config
 # python manage.py loaddata initial_trustcircles
 # python2 manage.py loaddata ctc_testusers # populate it with admin:admin and user:user
 python2 manage.py collectstatic --noinput
 gunicorn -b 0.0.0.0:${TC_DOCR_PORT} --workers=3 wsgi:application
