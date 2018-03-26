#!/bin/sh

echo "[i] deleting the redis container, csp-redis"
docker rm csp-redis

echo "[i] removing the data volume used by redis"
docker volume rm REDISDataVolume

echo "[i] deleting the redis docker image, csp-redis:1.0"

docker rmi csp-redis:1.0

echo [i] DONE!"
