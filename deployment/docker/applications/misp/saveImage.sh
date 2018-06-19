#!/usr/bin/env bash

rm image.tar.bz2

docker save csp-misp:1.0 mariadb -o image.tar
bzip2 -9 image.tar
