#!/usr/bin/env bash

CONTAINERNAME="$C_NAME"
SCRIPT="$C_SCRIPT"
EXTNAME="$C_EXTNAME"

echo "About to execute: "
echo "docker exec -it $CONTAINERNAME $SCRIPT $EXTNAME"

#docker exec -it $CONTAINERNAME $SCRIPT $EXTNAME
docker exec -i $CONTAINERNAME $SCRIPT $EXTNAME
RET=$?

if [ $RET -gt 0 ];
then
  echo "Script execution returned $RET, problem in installation?"
fi

exit $RET

