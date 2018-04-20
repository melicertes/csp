#!/bin/ash

if [ `whoami` != "root" ]; then
    echo "Installer can only execute as root"
    exit 2
fi

echo "Updating system"
apk --update add bash openntpd drill wget ca-certificates libressl python py2-pip py-jinja2 git && pip install j2cli[yaml] && update-ca-certificates
rc-update add ntpd
echo "NTPD_OPTS=\"-p pool.ntp.org\"" > /etc/conf.d/ntpd
rc-service ntpd start

echo "installing CSPINST"
install -D -o root -g root -m 0644 bashinator.lib.0.sh /usr/share/bashinator/bashinator.lib.0.sh
# install the bashinator cspinst application from the doc directory to the standard location
install -D -o root -g root -m 0644 bashinator.cfg.sh /etc/cspinst/bashinator.cfg.sh
install -D -o root -g root -m 0644 cspinst.cfg.sh /etc/cspinst/cspinst.cfg.sh
install -D -o root -g root -m 0644 cspinst.lib.sh /usr/share/cspinst/cspinst.lib.sh
install -D -o root -g root -m 0755 cspinst.sh /usr/bin/cspinst.sh
install -D -o root -g root -m 0755 csp_docker.tgz /etc/cspinst

echo "installing il server"
mkdir -p /opt/csp/il
#curl -o /opt/csp/il/il-server-exec.jar "http://central.preprod.melicertes.eu/repo-loads/integration-layer/il-server-exec.jar"

echo "fixing sysctl"
echo "vm.max_map_count=262144" >> /etc/sysctl.conf
echo "vm.swappiness=15" >> /etc/sysctl.conf

sysctl -p
