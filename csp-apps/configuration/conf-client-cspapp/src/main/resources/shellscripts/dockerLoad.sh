#!/bin/sh

echo "TAR_FILE=$TAR_FILE"
echo "WORK_DIR=$WORK_DIR"

CWD=$(pwd)
cd "$WORK_DIR"
docker load -i "$TAR_FILE"
RET=$?
cd "$CWD"
exit $?