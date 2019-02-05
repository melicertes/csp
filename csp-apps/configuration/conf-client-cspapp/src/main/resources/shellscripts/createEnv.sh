#!/bin/bash

function createEnvironment() {

    echo "$(fgrep XXXDOMAINXXX $ENVJSON)"
	echo "environment found: $ENVJSON $J2ENV " # $SITESC
	echo "About to replace $CSPNAME $CSPDOMAIN $INT_IP inside the configuration..."
	sed -i.bak1 "s/XXXDOMAINXXX/$CSPDOMAIN/" $ENVJSON
	sed -i.bak2 "s/XXXNAMEXXX/$CSPNAME/" $ENVJSON
	sed -i.bak3 "s/XXXIPXXX/$INT_IP/" $ENVJSON
	sed -i.bak4 "s#XXXMAILHOSTXXX#${MAIL_HOST}#g" "${ENVJSON}"
	sed -i.bak5 "s#XXXMAILPORTXXX#${MAIL_PORT}#g" "${ENVJSON}"
	sed -i.bak6 "s#XXXMAILUSERNAMEXXX#${MAIL_USERNAME}#g" "${ENVJSON}"
	sed -i.bak7 "s#XXXMAILPASSWORDXXX#${MAIL_PASSWORD}#g" "${ENVJSON}"
    sed -i.bak8 "s#XXXMAILSENDERXXX#${MAIL_SENDER_NAME}#g" "${ENVJSON}"
	sed -i.bak9 "s#XXXMAILSENDERMAILXXX#${MAIL_SENDER_EMAIL}#g" "${ENVJSON}"

    echo ""
    echo ""
    echo ""
    echo "${ENVJSON}"
    cat ${ENVJSON}

    echo ""
    echo ""
    echo ""

    ### verify j2 presence
	local J2=$(which j2)
	local CLI=$( $J2 -v 2>&1 >/dev/null |  grep j2cli|wc -l )

	if [ "$CLI" == "1"  ]; then
	    echo "j2 found in $J2 location; seems CLI capable"
	else
	    echo "j2 is not installed? "
	    echo "`which j2` --- is it installed properly?"
	    return 2
	fi

    echo "User home is set to $HOME"
    echo "--------------"

    ### generate env file (common has priority 0)
    echo "executing : $J2 $J2ENV $ENVJSON "
	$J2 $J2ENV $ENVJSON > $HOME/common.0.env

    echo "Env created in $HOME"
    ls -la $HOME | grep env
    echo "--------------"

    ## generate sites conf
	## $J2 $SITESC $ENVJSON  > $HOME/csp-sites.conf

    ### create docker network
	local DN=$(docker network ls |grep local.$CSPNAME.$CSPDOMAIN|wc -l)
	if [ "$DN" == "0" ]; then
		echo "Creating docker network local.$CSPNAME.$CSPDOMAIN"
		docker network create local.$CSPNAME.$CSPDOMAIN
	fi
	echo "Docker network (internal) should be local.$CSPNAME.$CSPDOMAIN"

	### create docker network
	local DN=$(docker network ls |grep installer_net|wc -l)
	if [ "$DN" == "0" ]; then
		echo "Creating docker network installer_net"
		docker network create installer_net
	fi
	echo "Docker installer network (internal) should be installer_net"
	docker network ls

	# are we sure the directory exists?
	echo "Creating CSP Sites directory (DELETE=$DELETE_CONTENTS)"
	if [ "$DELETE_CONTENTS" == "true" ]; then
    	rm -fr /opt/csp/apache2/csp-sites
    fi
    mkdir -p /opt/csp/apache2/csp-sites


    ls -l /opt/csp/apache2/csp-sites

	return 0
}

function createDockerVolumes() {
    ### make a dir here.
    mkdir -p /opt/csp/logs

    docker volume create SSLDatavolume
    local R1=$?
    docker run -d --rm -v SSLDatavolume:/ssl_data -v /opt/csp/apache2/ssl/:/mnt thanosa75/alpine-jdk8:slim sh -c "cp -r /mnt/server /mnt/ca  /ssl_data"
    local R2=$(( $? + $R1 ))

    return $R2
}

createEnvironment
RENV=$?
createDockerVolumes
RDOC=$?

echo "Environment $RENV Volumes $RDOC"
# we need to sum the exit codes to 0 for all to be well.
RALL=$(( $RDOC + $RENV ))
exit $RALL
