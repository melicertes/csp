#!/bin/bash

export TOOLS_HOME="/opt/ssoadm"

cd "$TOOLS_HOME"
service=$1
if [ -z "$service" ]
  then
    echo "Fatal Error: No argument supplied."
    exit -1
fi

echo "[i] Working with input: $service"

echo "[i] $service=>WebAgent_$service"
service=`echo "$service" | tr '[:upper:]' '[:lower:]'`

if [ -f "/opt/ssoadm/agent-config.tmpl.$service" ]; then
    echo "[i] Found template agent-config.tmpl.$service"
    cp /opt/ssoadm/agent-config.tmpl.$service /opt/ssoadm/agent-config
else
    echo "[i] Using default template"
    cp /opt/ssoadm/agent-config.tmpl /opt/ssoadm/agent-config
fi

sed -i "s/{APP}/${service}/g"  /opt/ssoadm/agent-config
sed -i "s/{DOMAIN}/$DOMAIN/g" /opt/ssoadm/agent-config

$TOOLS_HOME/openam/bin/ssoadm create-agent --realm "/" \
--adminid amadmin --password-file $TOOLS_HOME/pwd.txt \
--agenttype WebAgent  --agentname WebAgent_$service  \
--datafile $TOOLS_HOME/agent-config
echo "[i] Create WebAgent_$service for ${service} done."