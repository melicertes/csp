#!/usr/bin/env bash

echo "[i] create data volume (OAMDatavolume)"
docker volume create OAMDatavolume

docker volume create OAMOptvolume

docker volume create OAMWebappsvolume

echo "[i] done."
