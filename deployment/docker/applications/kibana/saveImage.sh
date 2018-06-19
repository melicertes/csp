#!/usr/bin/env bash

rm image.tar.bz2

docker save docker.elastic.co/kibana/kibana:5.4.0 -o image.tar
bzip2 -9 image.tar
