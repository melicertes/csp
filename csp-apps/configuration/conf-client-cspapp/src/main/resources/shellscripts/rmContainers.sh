#!/bin/bash

echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"

echo "Removing previous containers (if found) for $SERVICE_NAME"

CWD=$(pwd)
cd "$SERVICE_DIR"

#make sure the existing container is stopped.
echo "Removing (pre)existing containers..."
docker-compose rm -f -s &> /dev/null
RET=$?
echo "Done, returned $RET"
cd "$CWD"

exit $RET
