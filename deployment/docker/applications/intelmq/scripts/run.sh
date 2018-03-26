#!/bin/sh

set -e

echo "[i] starting the intlemq pipes ..."
sudo -u intelmq /usr/bin/intelmqctl start

rm -f /run/apache2/httpd.pid

echo "[i] starting the apache server for intelmq manager ..."
exec /usr/sbin/httpd -DFOREGROUND

