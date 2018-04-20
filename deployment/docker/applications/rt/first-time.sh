#!/bin/sh

#mkdir -p /opt/csp/rt
#chmod -R 0777 /opt/csp/rt

echo "[i] create data volumes to be used by rt"
docker volume create RTStateDatavolume
docker volume create RTDatavolume
docker volume create RTAdapterDBDatavolume
