#!/usr/bin/env bash

source common.sh

if [ "xx$1" == "xx" ]; 
then 
  echo "Please call: $0 <module directory>"
else 
  prepareModule $1
fi

