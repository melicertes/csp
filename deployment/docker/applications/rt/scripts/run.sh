#!/bin/sh

RTCONF="/opt/rt4/etc/RT_SiteConfig.pm"
MAXTRIES=20

wait4psql () {
echo "[i] Waiting for database to setup..."

export PGPASSWORD=$POSTGRES_PASSWORD
for i in $(seq 1 1 $MAXTRIES)
do
	echo "[i] Trying to connect to database: try $i..."
	psql -h ${RT_DB_HOST} -p ${POSTGRES_DOCR_PORT} -U ${POSTGRES_USER} -w -c 'SELECT version();'
	if [ "$?" = "0" ]; then
		echo "[i] Successfully connected to database!"
		break
	else
		if [ "$i" = "$MAXTRIES" ]; then
			echo "[e] You need to have container for database."
			exit 0
		else
			sleep 5
		fi
	fi
done
}

if [ -f /scripts/rt/rt.initialized ]; then
	echo "[i] RT already configured!"
else
	echo "[i] setting up RT-SiteConfig.pm"
        sed -i "s@__RT_WEB_URL__@https://rt.$DOMAIN/@g" /opt/rt4/etc/RT_SiteConfig.pm
        sed -i "s@__RT_WEB_DOMAIN__@rt.$DOMAIN@g" /opt/rt4/etc/RT_SiteConfig.pm
        sed -i "s@__RT_EMITTER_URL__@https://rt-adapter.$LOCAL_DOMAIN@g" /opt/rt4/etc/RT_SiteConfig.pm
        sed -i "s@__RT_CSP_NAME__@$CSP_NAME@g" /opt/rt4/etc/RT_SiteConfig.pm
        sed -i "s@__RT_TC_URL__@https://tc.$LOCAL_DOMAIN@g" /opt/rt4/etc/RT_SiteConfig.pm	
	touch /scripts/rt/rt.initialized
fi

if [ -f /scripts/rt/db.initialized ]; then
	echo "[i] Database already initialized. Not touching database!"
	wait4psql
else
	echo "[i] Database not initialized. Initializing now..."
	wait4psql

	echo "[i] Initializing database for RT"
	cd /tmp/rt-$RT_VERSION
        perl sbin/rt-setup-database \
                --action init \
                --dba ${POSTGRES_USER} \
                --dba-password ${POSTGRES_PASSWORD}

	echo "[i] Initializing database for RT::IR extension"
	cd /tmp/RT-IR-${RT_IR_VERSION}
        perl -Ilib -I/opt/rt4/local/lib -I/opt/rt4/lib /opt/rt4/sbin/rt-setup-database \
                --action insert \
                --datadir /tmp/RT-IR-${RT_IR_VERSION}/etc \
                --datafile /tmp/RT-IR-${RT_IR_VERSION}/etc/initialdata \
                --dba ${POSTGRES_USER} \
                --dba-password ${POSTGRES_PASSWORD} \
                --package RT::IR \
                --ext-version 4.0.1
	echo "[i] Initializing some CSP additional data"
	cd /tmp/rt-$RT_VERSION
	perl sbin/rt-setup-database \
		--action insert \
		--datafile /opt/rt4/etc/additional-initialdata.csp \
		--dba ${POSTGRES_USER} \
        --dba-password ${POSTGRES_PASSWORD}

	touch /scripts/rt/db.initialized
	#rm -rf /tmp/rt-$RT_VERSION 
	#rm -rf /tmp/RT-IR-$RT_IR_VERSION
fi

if [ ! -f /opt/rt4/var/log/CSP.RT-exc.log ]; then
	echo "[i] creating and setting up of /opt/rt4/var/log/CSP.RT-exc.log"
	mkdir -p /opt/rt4/var/log
	touch /opt/rt4/var/log/CSP.RT-exc.log
fi

chown -R lighttpd:lighttpd /opt/rt4/var/log	

echo "[i] Starting web daemon..."
lighttpd -f /etc/lighttpd/lighttpd.conf -D

