#!/bin/bash

J2TEMPL="$J2_TEMPLATE"
DATAJSON="$DATA_JSON"
J2OUTPUT="$J2_OUTPUT"

echo "Going into $WORK_DIR"
cd $WORK_DIR

function execj2() {

    echo "Executing..."
    echo "J2TEMPL=$J2TEMPL"
    echo "J2OUTPUT=$J2OUTPUT"
    echo "DATAJSON=$DATAJSON"

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
	$J2 "$J2TEMPL" "$DATAJSON" > "$J2OUTPUT"
	return $?
}


execj2
RENV=$?

echo "ComposeJ2 returned $RENV"
# we need to sum the exit codes to 0 for all to be well.
exit $RENV
