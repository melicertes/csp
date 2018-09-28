#!/usr/bin/env bash

docker volume create TCDatavolume
docker volume create WebAgentsDatavolume

docker run -d --rm -v SSLDatavolume:/ssl_data frolvlad/alpine-oraclejdk8:slim sh -c "mkdir -p /ssl_data/crl"

docker network  ls --format '{{.Name}}' | grep installer_net  > /dev/null
if [[ "$?" -gt 0 ]] ; then
    echo "Creating docker network installer_net"
    echo $(docker network create installer_net)
fi
