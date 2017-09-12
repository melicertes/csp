#!/bin/sh

function createEnvironment() {

    echo "$(fgrep XXXDOMAINXXX $ENVJSON)"
	echo "environment found: $ENVJSON $J2ENV $SITESC"
	echo "About to replace $CSPNAME $CSPDOMAIN $INT_IP inside the configuration..."
	sed -i.bak "s/XXXDOMAINXXX/$CSPDOMAIN/" $ENVJSON
	sed -i.bak2 "s/XXXNAMEXXX/$CSPNAME/" $ENVJSON
	sed -i.bak3 "s/XXXIPXXX/$INT_IP/" $ENVJSON
	sed -i.bak11 "s#XXXMAILHOSTXXX#${MAIL_HOST}#g" "${ENVJSON}"
	sed -i.bak12 "s#XXXMAILPORTXXX#${MAIL_PORT}#g" "${ENVJSON}"
	sed -i.bak13 "s#XXXMAILUSERNAMEXXX#${MAIL_USERNAME}#g" "${ENVJSON}"
	sed -i.bak14 "s#XXXMAILPASSWORDXXX#${MAIL_PASSWORD}#g" "${ENVJSON}"


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

    ### generate env file
	$J2 $J2ENV $ENVJSON > ~/.env
	cp ~/.env ~/env
    ## generate sites conf
	$J2 $SITESC $ENVJSON  > ~/csp-sites.conf

        ### create docker network
	local DN=$(docker network ls |grep local.$CSPNAME.$CSPDOMAIN|wc -l)
	if [ "$DN" == "0" ]; then
		echo "Creating docker network local.$CSPNAME.$CSPDOMAIN"
		docker network create local.$CSPNAME.$CSPDOMAIN
	fi
	echo "Docker network (internal) should be local.$CSPNAME.$CSPDOMAIN"
	docker network ls

	# are we sure the directory exists?
	rm -fr /opt/csp/apache2/csp-sites
    mkdir -p /opt/csp/apache2/csp-sites

    cp ~/csp-sites.conf /opt/csp/apache2/csp-sites
    echo "Site configuration copied"
    ls -l /opt/csp/apache2/csp-sites

	return 0
}

createEnvironment
