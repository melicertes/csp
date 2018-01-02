#!/usr/bin/env bash
echo "CSPNAME   = $CSPNAME"
echo "CSPDOMAIN = $CSPDOMAIN"
echo "DIR=$DIR"
echo "FIRST_TIME_SH=$FIRST_TIME_SH"

if [ "xx$FIRST_TIME_SH" == "xx" ];
then
  FIRST_TIME_SH="./first-time.sh"
else
  FIRST_TIME_SH="./$FIRST_TIME_SH"
fi


echo "first-time.sh: About to execute $FIRST_TIME_SH in $DIR"
cd "$DIR"
bash "$FIRST_TIME_SH"
RET=$?
echo "first-time.sh: returned $RET"
exit $RET
