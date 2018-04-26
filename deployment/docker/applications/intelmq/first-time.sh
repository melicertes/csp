#!/bin/sh

if [ ! -d /opt/csp/logs/imq ]; then
  echo "[i] create directory for imq logs /opt/csp/logs/imq"
  mkdir -p /opt/csp/logs/imq
fi

if [ ! -d /opt/csp/intelmq-fileinput ]; then
  echo "[i] create directory for imq bot input /opt/csp/intelmq-fileinput"
  mkdir -p /opt/csp/intelmq-fileinput
  echo "[i] change permissions for directory /opt/csp/intelmq-fileinput"
  chmod og+rw -R /opt/csp/intelmq-fileinput
fi

if [ ! -d /opt/csp/intelmq-fileoutput ]; then
  echo "[i] create directory for imq bot output /opt/csp/intelmq-fileouput"
  mkdir -p /opt/csp/intelmq-fileoutput
  echo "[i] change permissions for directory /opt/csp/intelmq-fileouput"
  chmod og+rw -R /opt/csp/intelmq-fileoutput
fi
#echo "[i] create data volumes to be used by intelmq"
#docker volume create SomeDatavolume
#
