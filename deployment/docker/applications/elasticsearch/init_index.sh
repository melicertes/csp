#!/usr/bin/env bash


/bin/bash /utils/init_csp_index.sh
/bin/bash /utils/restore.sh

ES_PID=`cat /tmp/elasticsearch.pid`
echo "Elastic Search PID is : $ES_PID"

echo "Waiting for 2 min before killing ES"
sleep 120

echo "Killing ES with PID : $ES_PID"
kill -SIGTERM $ES_PID