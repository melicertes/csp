#!/usr/bin/env bash
echo "[i] adding logrotate ;) "
apk add -q -U logrotate
if [ "$?" -gt 0 ]; 
then
  echo "[ERROR] logrotate could not be installed"
fi
echo "[i] adding jitsi logs directory..."
mkdir -p /opt/csp/logs_jitsi
chmod 777 /opt/csp/logs_jitsi
echo "[i] adding volume..."
docker volume create JitsiDataVolume


