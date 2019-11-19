#!/bin/sh

echo "[i] cleaning the rt-database stored on postgresql"
docker-compose run --rm --entrypoint /scripts/cleandb.sh rt

echo "[i] deleteing rt common files stored on /opt/csp/rt"
echo "y" | rm -f /opt/csp/rt/*

echo "[i] deleting the csp-rt container"
docker rm csp-rt

echo "[i] removing the docker volumes used by rt"
docker volume rm -f RTStateDatavolume 
docker volume rm -f RTDatavolume


echo "[i] removing the image csp-rt:1.0"
docker rmi csp-rt:1.0

