#!/bin/bash


echo "SERVICE_NAME=$SERVICE_NAME"
echo "SERVICE_DIR=$SERVICE_DIR"
echo "SERVICE_PRIO=$SERVICE_PRIO"
WAIT_SECONDS=60

CWD=$(pwd)
cd "$SERVICE_DIR"

# is_ready() checks and returns if process is ready
# returns:
#       0 - not ready yet
#       1 - ready
#       100 - error detected, abort
function is_ready() {
    return 1;
}

# monitor_wait() - monitors and waits using the prototype above or sourced from file
function monitor_wait() {

    local exitn=0;
    while [ $exitn -eq 0 ];
    do
        echo "Waiting $WAIT_SECONDS sec for $SERVICE_NAME to be ready....."
        sleep $WAIT_SECONDS;
        is_ready
        local retval=$?
        echo "Monitor returned $retval"
        if [ $retval -eq 1 ];
        then
            echo "monitor returned OK"
            exitn=1; #exit
        elif [ $retval -eq 100 ];
        then
            echo "Error condition $retval detected, NOT OK"
            extin=1; #exit
        fi
    done
}

if [ ! -e "proc-ready.source" ];
then
    echo "Sourcing update..."
    source ./proc_ready.source
fi

echo "Monitoring $SERVICE_NAME to start..."
monitor_wait


