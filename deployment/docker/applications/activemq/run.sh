#!/bin/sh

if [ -f "/opt/activemq/.amqinit" ] ; then
    echo "======================================="
    echo "ActiveMQ initialised - not first run"
    echo "======================================="
else
    cd /opt/activemq
    tar xf init.tar
    echo "=============================================="
    echo "ActiveMQ initial run - Extracted configuration"
    echo "=============================================="
    touch /opt/activemq/.amqinit
fi
echo ""
echo ""
echo ""
echo ""
bin/activemq console

