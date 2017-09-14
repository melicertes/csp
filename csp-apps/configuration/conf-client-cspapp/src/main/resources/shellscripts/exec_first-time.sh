#!/usr/bin/env bash
echo "CSPNAME   = $CSPNAME"
echo "CSPDOMAIN = $CSPDOMAIN"
echo "first-time.sh: About to execute "
./first-time.sh
RET=$?
echo "first-time.sh: returned $RET"
exit $RET
