#!/bin/bash

function createEnvironment() {


	echo "environment found: env: $ENVJSON j2env: $J2ENV siteconf: $SITESC prefix: $MODULE_PREFIX"
	echo "About to replace $CSPNAME $CSPDOMAIN $INT_IP inside the configuration..."
	sed -i.bak "s/XXXDOMAINXXX/$CSPDOMAIN/" $ENVJSON
	sed -i.bak2 "s/XXXNAMEXXX/$CSPNAME/" $ENVJSON
	sed -i.bak3 "s/XXXIPXXX/$INT_IP/" $ENVJSON

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

    ### generate env file
	$J2 $J2ENV $ENVJSON > $HOME/$MODULE_PREFIX.env
    ## generate sites conf
	$J2 $SITESC $ENVJSON  > $HOME/csp-sites.$MODULE_PREFIX.conf

	return 0
}


createEnvironment
RENV=$?

echo "Environment $RENV"
# we need to sum the exit codes to 0 for all to be well.
exit $RENV
