#!/usr/bin/env bash

  echo ""
  echo ""
  echo " Create Backup directory"
  echo ""
  echo ""
  echo ""


curl -XPUT 'localhost:9200/_snapshot/cspbackup' -d'
{
    "type": "fs",
    "settings": {
      "compress": true,
      "location": "/backup"
    }
}
'

  echo ""
  echo ""
  echo " Delete old backups"
  echo ""
  echo ""
  echo ""


curl -XDELETE localhost:9200/.kibana
curl -XDELETE localhost:9200/.kibana4logstash

  echo ""
  echo ""
  echo " Restore from snapshot"
  echo ""
  echo ""
  echo ""


curl -XPOST 'localhost:9200/_snapshot/cspbackup/cspsnapshot/_restore?pretty' -H 'Content-Type: application/json' -d'
{
  "indices": ".kibana,.kibana4logstash",
  "ignore_unavailable": true,
  "include_global_state": true
}
'