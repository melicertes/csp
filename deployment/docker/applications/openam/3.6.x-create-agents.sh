#!/usr/bin/env bash

#
# script ONLY for purposes of installing
# the version 3.6.x of OAM from scratch on the 
# existing CSP installation
#

echo "[i] create web agents on OAM"

docker exec -it csp-oam script /dev/null -c "echo \"[i] proceeding ...\"
 create-agent.sh anon-ui; \
 create-agent.sh integration-ui; \
 create-agent.sh imq; \
 create-agent.sh logs; \
 create-agent.sh misp-ui; \
 create-agent.sh rt; \
 create-agent.sh search; \
 create-agent.sh tc; \
 create-agent.sh teleconf; \
 create-agent.sh teleconf-ui; \
 create-agent.sh vcb-teleconf"

echo "[i] DONE!"
  


