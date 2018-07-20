#!/usr/bin/env bash

rm image.tar.bz2

docker save csp-python27-viper:1.0 -o image.tar
bzip2 -9 image.tar
