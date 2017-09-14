#!/bin/bash

echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"
echo "SERVICE_PRIO=$SERVICE_PRIO"

echo "Starting $SERVICE_NAME"

CWD=$(pwd)
cd "$SERVICE_DIR"

#make sure the existing container is stopped.
docker-compose stop &> /dev/null
RET=$?

if [[ $RET -gt 0 ]];
then
    docker-compose kill
    RET=$?
fi

exit $RET
