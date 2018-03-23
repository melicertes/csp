#!/bin/ash

function waitForApache() {

    count=0

    echo "Waiting for apache...."
    while [ $count -lt 100 ]; do
    curl -I https://auth.$DOMAIN --insecure
	ret=$?
	if [ $ret -eq 0 ]; then
	  echo "Apache started"
          count=111 #we get out of the loop this way
	  return 0
	else
	  echo "Apache is still down; sleeping to retry in 60sec [$count / 5]"
	  count=$(( $count + 1 ))
	  sleep 60
	fi
    done
# reaching here is a failure!
    return 1
}


waitForApache
retVal=$?

if [ ${retVal} -eq 0 ]; then
    python2 manage.py dsl_sync --interval 60
else
    echo "Configuration FAILURE"
    exit 1
fi
