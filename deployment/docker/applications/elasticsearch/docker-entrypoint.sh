#!/bin/bash

if [ ! -f /opt/config/.cfg ]; then
    echo "Initialize ElasticSearch"
    mkdir -p /opt/config/
    touch /opt/config/.cfg
    bin/es-docker-init & /bin/bash /utils/wait-for-it.sh -t 0 localhost:9200 -- /utils/init_index.sh
fi

echo "Successful ElasticSearch Initialization"

exec "$@"