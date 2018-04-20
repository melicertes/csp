#!/usr/bin/env bash

rm image.tar.bz2
docker save csp-elasticsearch:1.0 -o image.tar
bzip2 -9 image.tar
