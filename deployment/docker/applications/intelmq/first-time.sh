#!/bin/sh

if [ ! -d /opt/csp/logs/imq ]; then
  echo "[i] create directory for imq logs /opt/csp/logs/imq"
  mkdir -p /opt/csp/logs/imq
fi

#echo "[i] create data volumes to be used by intelmq"
#docker volume create SomeDatavolume
#
