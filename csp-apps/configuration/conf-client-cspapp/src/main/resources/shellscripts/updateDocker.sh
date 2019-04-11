#!/usr/bin/env bash

UN=`uname`
if [ "$UN" == "Linux" ];
then
    echo "Linux OS detected"
    echo ""
    echo ""
else
    echo "platform $UN not supported"
    exit 100
fi

if [ -f "/opt/cspinst/.docker.updated.1" ];
then
  echo "Docker version is `docker version`"
  exit 0
fi

###check if update has already completed

## we will stop docker here. We assume this only gets executed at boot
## so no docker services are running. To be safe, we zap it at the end.
service docker stop
RST=$?
while [ $RST -gt 0 ];
do
    echo "Stopping docker..."
    service docker stop
done
service docker zap

source /etc/os-release
if [[ "$VERSION_ID" == "3.9"* ]];
then
   echo "OS update not necessary"
else
   echo "OS update is needed, OS now is $VERSION_ID" >> /opt/cspinst/os-update.1
   echo "http://dl-4.alpinelinux.org/alpine/latest-stable/main" > /etc/apk/repositories
   echo "http://dl-4.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories
   echo "http://dl-4.alpinelinux.org/alpine/edge/community" >> /etc/apk/repositories
   echo "Updating repositories"
   apk update >> /opt/cspinst/os-update.1
   apk upgrade >> /opt/cspinst/os-update.1
   echo "About to reboot host at `date` " >> /opt/cspinst/os-update.1
   reboot >> /opt/cspinst/os-update.1
fi


echo "Updating docker - checking version"
DOCKER_V=`docker -v | awk '{ print $3 }'| awk -F- '{print $1}'`
if [[ "$DOCKER_V" == "18.09"* ]];
then
    SUBVER=`echo $DOCKER_V | awk -F. '{print $3}'`

    if [[ "$SUBVER" > 2 ]];
    then
        echo "Docker version is `docker -v` - resuming operations" >> /opt/cspinst/docker-update.1
        echo "`date`" > /opt/cspinst/.docker.updated.1
        service docker start
        exit 0
    else
        echo "Docker version is `docker -v` - trying to upgrade"
    fi
fi
echo "`date` PRE: Docker now -> `docker -v`" >> /opt/cspinst/docker-update.1

apk add 'docker>=18.09.3-r0'
docker -v
RET=$?
if [ $RET -eq 0 ];
then
    echo "`date` POST: Docker now -> `docker -v`" >> /opt/cspinst/docker-update.1
else
    echo "Failed to add docker - manual intervention required"
    exit 2
fi
service docker start
