#!/bin/sh

echo "[i] reset the configuration of the RT, do not touch the data stored in DB"

docker-compose run --rm --entrypoint /scripts/uninit-rt.sh csp-rt

echo "[i] deleting the container csp-rt"

docker rm csp-rt

echo "[i] deleting the image csp-rt:1.0"

docker rmi csp-rt:1.0

