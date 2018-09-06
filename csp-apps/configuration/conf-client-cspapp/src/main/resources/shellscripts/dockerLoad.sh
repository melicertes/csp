#!/bin/bash

echo "ARCHIVE_FILE=$ARCHIVE_FILE"
echo "WORK_DIR=$WORK_DIR"

function logRotate() {
    echo -n "adding logrotate entry ..."
cat <<EOF > /etc/logrotate.d/docker-container-logs
/var/lib/docker/containers/*/*-json.log {
  rotate 7
  daily
  size 10M
  compress
  missingok
  delaycompress
  copytruncate
}
EOF
    echo "done"
}

#lets verify that container logs logrotate script exists
#otherwise we risk that the logs grow to fill disk.
if [ ! -f /etc/logrotate.d/docker-container-logs ] ;
then
    logRotate
fi


CWD=$(pwd)
cd "$WORK_DIR"

if [[ "$ARCHIVE_FILE" == *.tar.bz2 ]] ;
then
    bzcat "$ARCHIVE_FILE" | docker load
    RET=$?
else
    cat "$ARCHIVE_FILE" | docker load
    RET=$?
fi
cd "$CWD"
exit $RET