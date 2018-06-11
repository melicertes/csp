#!/bin/bash
echo "Starting.... "
echo "DOMAIN=$DOMAIN"
echo "STUN=$STUN"
echo "BRIDGE_IP=$BRIDGE_IP"
echo "BRIDGE_TCP_PORT=$BRIDGE_TCP_PORT"
echo "BRIDGE_UDP_PORT=$BRIDGE_UDP_PORT"
echo ""

# Generate secrets if this is the first run
if [ ! -f /.first-run ]; then
echo "This is a first run...."

touch /.first-run
export JICOFO_SECRET=`pwgen -s 16 1`
export JVB_SECRET=`pwgen -s 16 1`
export FOCUS_SECRET=`pwgen -s 16 1`
export LOCAL_IP=`grep $(hostname) /etc/hosts | cut -f1`

# Substitute configuration
for VARIABLE in `env | cut -f1 -d=`; do
  sed -i "s={{ $VARIABLE }}=${!VARIABLE}=g" /etc/jitsi/*/* /etc/nginx/nginx.conf /etc/prosody/prosody.cfg.lua
done

/etc/init.d/prosody restart
prosodyctl register focus "auth.$DOMAIN" $FOCUS_SECRET

else
echo "Not first run, things already configured"
fi

# TODO: improve process management
/etc/init.d/prosody restart
/etc/init.d/jicofo restart
/etc/init.d/jitsi-videobridge restart
nginx -g 'daemon off;' &
tail -F /var/log/nginx/*log

