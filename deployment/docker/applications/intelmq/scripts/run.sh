#!/bin/sh

set -e

APACHE_PID_FILE=/var/run/apache2/apache2.pid

echo "[i] starting the intlemq pipes ..."
sudo chmod -R ugo+w /opt/intelmq/var/log
sudo -u intelmq /usr/local/bin/intelmqctl start
echo "[i] starting the intlemq pipes done"

echo "[i] starting the apache server for intelmq manager ..."
if [ -f "$APACHE_PID_FILE" ]; then
    sudo rm "$APACHE_PID_FILE"
fi
exec /usr/sbin/apache2ctl -D FOREGROUND


