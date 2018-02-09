#!/bin/bash

docker save csp-jitsi:1.0 | bzip2 > image.tar.bz2
