#!/bin/bash

BIN_PATH=`dirname "$0"`

cd $BIN_PATH/../

zip -j $1 dist/*
zip -r $1 tc -x "*.pyc" -x "tc/.cache*" -x "*__pycache__*"
