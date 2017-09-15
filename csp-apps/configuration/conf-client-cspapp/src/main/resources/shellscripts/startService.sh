#!/bin/bash

echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"
echo "SERVICE_PRIO=$SERVICE_PRIO"

echo "Starting $SERVICE_NAME"

CWD=$(pwd)
cd "$SERVICE_DIR"

#make sure the existing container is stopped.
/usr/local/bin/docker-compose kill &> /dev/null

#create and start detached
/usr/local/bin/docker-compose up -d
RET=$?

echo "Priority $SERVICE_PRIO"
if [[ $SERVICE_PRIO -gt 1000 ]];
then
    echo "Waiting for 5 minutes... service starting..."
    sleep 300
fi
cd "$CWD"

exit $RET
