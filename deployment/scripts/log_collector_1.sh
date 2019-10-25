#!/bin/bash
LTMP=/tmp/log_temp

echo "Log collector for CSP"
echo "------------------------------------"
echo ""
echo "1. clearing temp collector directory"
rm -fr $LTMP
mkdir -p $LTMP
echo "2. gathering logs from /opt/csp/logs"
YTD=$(find /opt/csp/logs -name "*`date +%Y-%m-%d -d yesterday`*")
mkdir -p $LTMP/csplogs && cp /opt/csp/logs/CSP.*-aud.log /opt/csp/logs/CSP.*-exc.log $YTD /opt/csp/logs/trust*log $LTMP/csplogs
mkdir -p $LTMP/system && cp /tmp/console.log /tmp/spring*log $LTMP/system
mkdir -p $LTMP/conf && cp /root/*conf /root/*env /opt/cspinst/.*.db $LTMP/conf
DCNT=$( docker ps -a --format "table {{.Names}} "|grep csp )
mkdir -p $LTMP/docker
echo -n "2.1 Saving console docker logs:"
for NM in $DCNT ; do echo -n " $NM " ; docker logs $NM > $LTMP/docker/$NM.log 2>&1 ; done
echo " --- done."
du -sh /opt/csp/* > $LTMP/report.txt
df -h /opt/csp >> $LTMP/report.txt
echo "----docker----" >> $LTMP/report.txt
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Ports}}\t{{.Status}}" >> $LTMP/report.txt
echo "" >> $LTMP/report.txt
docker version >> $LTMP/report.txt
echo "" >> $LTMP/report.txt
echo "`find /opt/csp/apache2 -exec ls -l \{\} \\; ` " >>$LTMP/report.txt
echo "" >> $LTMP/report.txt
echo "`date` " >> $LTMP/report.txt
FN=$( echo "`pwd`/support-logs-`date +%Y%m%d`.tbz")
echo "3. Packaging into $FN ...."
rm -f $FN
tar cfj $FN -C $LTMP . >/dev/null 2>&1
echo "4. Done -> file now created (exitcode: $?)"
ls -hl $FN
rm -fr $LTMP/*
