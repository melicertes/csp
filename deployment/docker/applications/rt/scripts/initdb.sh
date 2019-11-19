#!/bin/sh

echo "[i] Initializing database for RT"
cd /opt/rt4/etc
perl ../sbin/rt-setup-database \
	--action init \
        --dba ${POSTGRES_USER} \
        --dba-password ${POSTGRES_PASSWORD} 

echo "[i] Initializing database for RT::IR"
cd /opt/rt4/local/plugins/RT-IR
perl -Ilib -I/opt/rt4/lib /opt/rt4/sbin/rt-setup-database \
	--action insert \
        --datadir /tmp/RT-IR-${RT_IR_VERSION}/etc \
        --datafile /tmp/RT-IR-${RT_IR_VERSION}/etc/initialdata \
        --dba ${POSTGRES_USER} \
        --dba-password ${POSTGRES_PASSWORD} \
        --package RT::IR \
        --ext-version 4.0.1

echo "[i] Initializing some additonal data for CSP"
cd /opt/rt4/etc
perl ../sbin/rt-setup-database \
        --action insert \
        --datafile /opt/rt4/etc/additional-initialdata.csp \
        --dba ${POSTGRES_USER} \
        --dba-password ${POSTGRES_PASSWORD}

touch /scripts/rt/db.initialized

echo "[i] DONE!"

