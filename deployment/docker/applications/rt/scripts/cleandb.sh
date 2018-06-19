#!/bin/sh

echo "[i] droping and initializing the database of rt instance - reset state"

if [ ! -f /scripts/rt/rt.initialized ]; then
	echo "[i] Initializing RT config first"
	/scripts/initrt.sh
	touch /scripts/rt/rt.initialized
fi

if [ -f /scripts/rt/db.initialized ]; then
	echo "[i] database exists -> drop it now"
	echo "y" | /scripts/dropdb.sh
fi

echo "[i] DONE!"

