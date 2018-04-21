#!/bin/bash

echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"

echo "Removing previous containers (if found) for $SERVICE_NAME"

CWD=$(pwd)
cd "$SERVICE_DIR"

#make sure the existing container is stopped.
echo "Killing containers for $SERVICE_NAME (pwd=`pwd`)"
docker-compose kill
RET=$?
echo "Kill done, returned $RET"
echo "Removing (pre)existing containers...(pwd=`pwd`)"
docker-compose rm -f -s
RET=$?
echo "Remove done, returned $RET"
cd "$CWD"

exit $RET
