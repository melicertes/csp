#!/usr/bin/env bash

rm image.tar.bz2

docker save csp-openam:1.0-Alpha-orcl -o image.tar
bzip2 -9 image.tar
