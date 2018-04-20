#!/usr/bin/env bash

docker volume create TCDatavolume
docker run -d --rm -v TCDatavolume:/mnt -v "$(pwd)/tc":/tc csp-alpine35glibc:1.0 sh -c "rm -rf /mnt/*"
docker run -d --rm -v TCDatavolume:/mnt -v "$(pwd)/tc":/tc csp-alpine35glibc:1.0 sh -c "cp -a /tc/. /mnt/"

