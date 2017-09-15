#!/usr/bin/env bash
echo "CSPNAME   = $CSPNAME"
echo "CSPDOMAIN = $CSPDOMAIN"
echo "DIR=$DIR"
echo "first-time.sh: About to execute in $DIR"
cd "$DIR"
./first-time.sh
RET=$?
echo "first-time.sh: returned $RET"
exit $RET
