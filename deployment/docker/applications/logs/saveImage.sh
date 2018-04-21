#!/usr/bin/env bash

rm image.tar.bz2

docker save docker.elastic.co/beats/filebeat:5.6.0 docker.elastic.co/logstash/logstash:5.6.0 -o image.tar
bzip2 -9 image.tar
