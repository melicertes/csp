#!/usr/bin/env bash

docker volume create AMQDatavolume
docker volume create AMQConfigVolume

mkdir -p /opt/csp/logs_activemq/
