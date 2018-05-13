#!/usr/bin/env bash

docker volume create MISPDatavolume
docker volume create MISPAdapterDatavolume
docker volume create MYSQLDatavolume
docker volume create MISPSharedDatavolume
docker volume create MISPStateDatavolume
