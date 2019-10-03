#!/usr/bin/env bash
echo "[i] adding logrotate ;) "
apk add -q -U logrotate
if [ "$?" -gt 0 ]; 
then
  echo "[ERROR] logrotate could not be installed"
fi

cat > /etc/logrotate.d/jitsi-logrotate << EOF
/opt/csp/logs_jitsi/*.log {
  rotate 15
  daily
  size 10M
  compress
  delaycompress
  missingok
  copytruncate
  su root
}
EOF

echo "[i] adding jitsi logs directory..."
mkdir -p /opt/csp/logs_jitsi
chmod 664 /opt/csp/logs_jitsi

echo "[i] adding volume..."
docker volume create JitsiDataVolume


