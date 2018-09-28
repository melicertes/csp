#!/bin/bash
# Start OpenAM
# To create a persistent configuration mount a data volume on /openam/root

#  Point instance dir at /root/openam
mkdir -p /root/.openamcfg
cat >/root/.openamcfg/AMConfig_usr_local_tomcat_webapps_openam_ <<HERE
/root/openam
HERE

sed -i "s/{DOMAIN}/$DOMAIN/g" /opt/ssoadm/openam-config.properties

if [ ! -f /root/.csp ]; then
    echo "[i] Initializing OpenAM!!"
    export CATALINA_PID=/tmp/tomcat.pid
    touch /tmp/tomcat.pid
    touch /root/.csp
    /usr/local/tomcat/bin/catalina.sh start
    /bin/bash /opt/ssoadm/post-config-openam.sh
    /usr/local/tomcat/bin/catalina.sh stop 20 -force
    echo "[i] Waiting for 30 sec before restarting tomcat"
    sleep 30
fi

if [ -f /tmp/updates/update.sh ]; then
    echo "[i] move update.sh into ${TOOLS_HOME}"
    mv /tmp/updates/update.sh ${TOOLS_HOME}/update.sh
    chmod +x ${TOOLS_HOME}/update.sh
    echo "[i] execute ${TOOLS_HOME}/update.sh"
    ${TOOLS_HOME}/update.sh
fi

cd /usr/local/tomcat
bin/catalina.sh run

