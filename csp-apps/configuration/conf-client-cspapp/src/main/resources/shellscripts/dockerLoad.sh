#!/bin/bash

echo "ARCHIVE_FILE=$ARCHIVE_FILE"
echo "WORK_DIR=$WORK_DIR"

CWD=$(pwd)
cd "$WORK_DIR"

if [[ "$ARCHIVE_FILE" == *.tar.bz2 ]] ;
then
    bzcat "$ARCHIVE_FILE" | docker load
    RET=$?
else
    cat "$ARCHIVE_FILE " | docker load
    RET=$?
fi
cd "$CWD"
exit $?