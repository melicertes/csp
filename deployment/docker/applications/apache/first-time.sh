#!/usr/bin/env bash

docker volume create TCDatavolume
docker volume create WebAgentsDatavolume

docker network  ls --format '{{.Name}}' | grep installer_net  > /dev/null
if [[ "$?" -gt 0 ]] ; then
    echo "Creating docker network installer_net"
    echo $(docker network create installer_net)
fi
