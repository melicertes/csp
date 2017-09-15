#!/bin/bash

echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"
echo "SERVICE_PRIO=$SERVICE_PRIO"

echo "Stopping $SERVICE_NAME"

CWD=$(pwd)
cd "$SERVICE_DIR"

#make sure the existing container is stopped.
/usr/local/bin/docker-compose stop &> /dev/null
RET=$?

if [[ $RET -gt 0 ]];
then
    /usr/local/bin/docker-compose kill
    RET=$?
fi

exit $RET
