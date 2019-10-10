#!/usr/bin/env bash

rm image.tar.bz2
docker save csp-misp:1.0 mariadb | bzip2 -9  > image.tar.bz2

