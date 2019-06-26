#!/usr/bin/env bash
./build.sh
rm image.tar.bz2
docker save csp-activemq:1.0.1 -o image.tar
bzip2 -9 image.tar
