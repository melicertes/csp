#!/usr/bin/env bash

docker build -t ocredis:1.0 -f Dockerfile.redis .
docker build -t owncloud:1.0 -f Dockerfile.owncloud .
docker build -t ocdb:1.0 -f Dockerfile.mariadb .
