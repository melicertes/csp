#!/usr/bin/env bash
echo "[i] building..."
./build.sh
echo "[i] removing previous..."
rm image.tar.bz2 >/dev/null 2>&1
echo "[i] saving images to tarfile..."
docker save owncloud:1.0 ocdb:1.0 ocredis:1.0 -o image.tar
echo "[i] compressing..."
bzip2 -9 image.tar
