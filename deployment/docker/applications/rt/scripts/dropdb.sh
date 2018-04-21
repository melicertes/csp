#!/bin/sh

echo "[i] droping database"

/opt/rt4/sbin/rt-setup-database \
	--action drop \
        --dba ${POSTGRES_USER} \
        --dba-password ${POSTGRES_PASSWORD}

rm -f /scripts/rt/db.initialized
