#!/usr/bin/env bash
source common.sh

for i in $( ls -d */ ); do
  echo "working with : $i"
  prepareModule $i 
  if [[ "$?" -gt 0 ]]; 
  then
    echo "Error while zipping module for directory $i"
  fi
done
