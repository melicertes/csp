#!/bin/bash

echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"
echo "SERVICE_PRIO=$SERVICE_PRIO"

echo "Starting $SERVICE_NAME"

CWD=$(pwd)
cd "$SERVICE_DIR"

#make sure the existing container is stopped.
docker-compose kill &> /dev/null

#create and start detached
docker-compose up -d
RET=$?

#echo "Priority $SERVICE_PRIO"
#if [[ $SERVICE_PRIO -gt 1000 ]];
#then
#    echo "Waiting for 5 minutes... service starting..."
#    sleep 300
#fi

sleep 5
echo "Status of service ${SERVICE_NAME} :"
docker-compose ps

cd "$CWD"

exit $RET
