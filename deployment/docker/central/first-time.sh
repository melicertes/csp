#!/usr/bin/env bash

docker network create installer_net
docker volume create CGF_SERVER_DATA_VOLUME
docker volume create CGF_CLIENT_DATA_VOLUME
docker volume create CGF_CLIENT_ROOT_VOLUME
