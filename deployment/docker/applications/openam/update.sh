#!/bin/bash

# installing an update to current OAM installation

UPDATE_NAME=update-001

if [ ! -f /root/.$UPDATE_NAME ]; then
  echo "[i] performing update ..."
  touch /root/.${UPDATE_NAME}
  echo "[i] starting tomcat and waiting for 45 seconds before continue ..."
  /usr/local/tomcat/bin/catalina.sh start
  sleep 45
  echo "[i] do update now!"

  BATCH_FNAME=${UPDATE_NAME}.batch

  # update the server config -> workaround for cache issue with user data
  echo "[i] update the server config configuration"
  echo "update-server-cfg -s http://csp-oam:8080/openam -a com.iplanet.am.sdk.caching.enabled=false" > /tmp/${BATCH_FNAME}
  echo "update-server-cfg -s http://csp-oam:8080/openam -a com.sun.identity.sm.cache.enabled=true" >> /tmp/${BATCH_FNAME}
  echo "[i] update the server config configuration done!"

  # update the embedded datastore -> workaround for cache issue with user data
  echo "[i] update the datastore"
  echo "update-datastore -e \"/\" -m \"embedded\" -a \"sun-idrepo-ldapv3-config-psearchbase=\"" >> /tmp/${BATCH_FNAME}

  echo "[i] performing batch command for ${BATCH_FNAME} ..."
$TOOLS_HOME/openam/bin/ssoadm do-batch --batchfile /tmp/${BATCH_FNAME} --adminid amadmin --password-file $TOOLS_HOME/pwd.txt
  echo "[i] update the datastore done!"

  export CATALINA_PID=/tmp/tomcat-${UPDATE_NAME}.pid
  touch ${CATALINA_PID}

  echo "[i] stoping tomcat again and waiting 45 seconds before continue ..."
  /usr/local/tomcat/bin/catalina.sh stop 20 -force
  sleep 45
  echo "[i] update done"
fi
