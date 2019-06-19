#!/usr/bin/env bash

CHKPOINT42="/etc/.amq.42"
if [ -f "$CHKPOINT42" ]; then
  echo "[x] initialised for 4.2.x"	
else
  docker volume rm AMQDatavolume
  echo "[x] 4.2.x init complete"
fi

docker volume create AMQDatavolume
docker volume create AMQConfigVolume

mkdir -p /opt/csp/logs_activemq/
