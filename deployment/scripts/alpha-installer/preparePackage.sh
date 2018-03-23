#!/bin/bash

WD="$(pwd)"
rm csp_docker.tgz
cd ../..
tar --exclude='*intrademo/*' --exclude='._*' --exclude='.DS*' --exclude 'scripts/servers*' -cvzf /tmp/requirements.tgz *
cd "$WD"
mv /tmp/requirements.tgz csp_docker.tgz
