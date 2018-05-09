#!/usr/bin/env bash

echo "[i] removing the csp-oam container"
docker rm csp-oam

echo "[i] removing the docker volumes"
docker volume rm OAMDatavolume

echo "[i] create data volume (OAMDatavolume)"
docker volume create OAMDatavolume

echo "[i] done."
