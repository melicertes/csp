#!/usr/bin/env bash
echo "CSPNAME   = $CSPNAME"
echo "CSPDOMAIN = $CSPDOMAIN"
echo "DIR=$DIR"
echo "LAST_TIME_SH=$LAST_TIME_SH"

if [ "xx$LAST_TIME_SH" == "xx" ];
then
  LAST_TIME_SH="./last-time.sh"
else
  LAST_TIME_SH="./$LAST_TIME_SH"
fi

echo "last-time.sh: About to execute $LAST_TIME_SH in $DIR"
cd "$DIR"
bash "$LAST_TIME_SH"
RET=$?
echo "first-time.sh: returned $RET"
exit $RET
