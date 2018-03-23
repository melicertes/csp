#!/usr/bin/env bash
cp -r anonymization ~/
cp -r apache ~/
mv ~/apache ~/apache2
cp -r configuration ~/
cp -r elasticsearch ~/
cp -r integrationlayer ~/
cp -r intelmq ~/
cp -r jitsi ~/
cp -r kibana ~/
cp -r logs ~/
cp -r mocknode ~/
cp -r openam ~/
cp -r owncloud ~/
cp -r postgres ~/
cp -r trustcircles ~/

cp ~/.env ~/anonymization
cp ~/.env ~/apache2
cp ~/.env ~/configuration
cp ~/.env ~/elasticsearch
cp ~/.env ~/integrationlayer
cp ~/.env ~/intelmq
cp ~/.env ~/jitsi
cp ~/.env ~/kibana
cp ~/.env ~/logs
cp ~/.env ~/mocknode
cp ~/.env ~/openam
cp ~/.env ~/owncloud
cp ~/.env ~/postgres
cp ~/.env ~/trustcircles