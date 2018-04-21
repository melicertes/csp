#!/bin/sh

echo "[i] reset the configuration state of RT"

if [ -f /scripts/rt/rt.initialized ]; then
	rm -f /scripts/rt/rt.initialized
fi
