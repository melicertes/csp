#!/bin/sh

echo "[i] removing the csp-oam container"
docker rm csp-oam

echo "[i] removing the docker volumes"
docker volume rm OAMDatavolume 

docker volume rm OAMOptvolume

docker volume rm OAMWebappsvolume

echo "[i] removing the image csp-openam:1.0"
docker rmi csp-openam:1.0

echo "[i] DONE!"

