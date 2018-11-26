#!/usr/bin/env bash

#
# clean up script ONLY for purposes of installing
# the version 3.6.x of OAM from scratch on the 
# existing CSP installation
#

echo "[i] removing the csp-oam container"
docker rm csp-oam

echo "[i] removing the docker volumes"
docker volume rm OAMDatavolume

