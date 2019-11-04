#!/bin/sh

#set -e

APACHE_PID_FILE=/var/run/apache2/apache2.pid
INTELMQ_UPGRADE_FILE=/scripts/intelmq.upgraded
INTELMQ_UPGRADE_HARMON=/scripts/intelmq.upgraded.harmon
INTELMQ_INIT_FILE=/scripts/intelmq.init

echo "[i] starting the intlemq pipes ..."
sudo chmod -R ugo+w /opt/intelmq/var/log

if [ -f "$INTELMQ_INIT_FILE" ]; then
	echo "[i] INTELMQ already configured!"
else
	echo "[i] call intelmq setup"
        intelmqsetup	
	sudo touch "$INTELMQ_INIT_FILE"
fi


if [ -f "$INTELMQ_UPGRADE_FILE" ]; then
	echo "[i] INTELMQ already upgraded!"
else
	echo "[i] call intelmq upgrade"
	sudo touch "$INTELMQ_UPGRADE_FILE"
        sudo -u intelmq /usr/local/bin/intelmqctl upgrade-config
	echo "[i] create initial state file done."
        sudo -u intelmq /usr/local/bin/intelmqctl upgrade-config
	echo "[i] update the harmonization.conf to verion 2.1.1"
        sudo -u intelmq cp /opt/intelmq/etc/examples/harmonization.conf /opt/intelmq/etc/
        sudo -u intelmq /usr/local/bin/intelmqctl check
fi


if [ -f "$INTELMQ_UPGRADE_HARMON" ]; then
	echo "[i] INTELMQ harmonization is already upgraded!"
else
	echo "[i] upgrade intelmq harmonization to 2.1.1"
	sudo touch "$INTELMQ_UPGRADE_HARMON"
        sudo -u intelmq cp /opt/intelmq/etc/examples/harmonization.conf /opt/intelmq/etc/
        sudo -u intelmq /usr/local/bin/intelmqctl check
fi


sudo -u intelmq /usr/local/bin/intelmqctl start
echo "[i] starting the intlemq pipes done"

echo "[i] starting the apache server for intelmq manager ..."
if [ -f "$APACHE_PID_FILE" ]; then
    sudo rm "$APACHE_PID_FILE"
fi
exec /usr/sbin/apache2ctl -D FOREGROUND


