#!/bin/bash

#rsync -va /home/csp/github/csp/csp-apps/tc/tc/ ./tc
# make relative
rsync -va ../../../../csp-apps/tc/tc/ ./tc
echo "completed"
