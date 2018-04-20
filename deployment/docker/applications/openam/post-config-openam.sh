#!/bin/bash
# Post config OpenAM

export TOOLS_HOME="/opt/ssoadm"

cd "$TOOLS_HOME"
echo "[i] Running Configurator..."
# Wait for OpenAM to come up before configuring it
# args  $1 - server URL
# args  $2 - DEPLOYMENT_URI

function wait_for_openam
{
	T="$1$2/config/options.htm"

	until $(curl --output /dev/null --silent --head --fail $T); do
		echo "[i] Waiting for OpenAM server at $T "
        sleep 5
	done
	# Sleep an additonal time in case DJ is not quite up yet
	echo "[i] About to begin configuration in 45 seconds"
	sleep 45
}


SERVER_URL=$(awk -F "=" '/SERVER_URL/ {print $2}' openam-config.properties)
DEPLOYMENT_URI=$(awk -F "=" '/DEPLOYMENT_URI/ {print $2}' openam-config.properties)

wait_for_openam $SERVER_URL $DEPLOYMENT_URI
java -jar openam-configurator-tool-13.0.0.jar --file openam-config.properties 

echo '[i] Post config openam done.' 


ADMIN_PWD=$(awk -F "=" '/ADMIN_PWD/ {print $2}' openam-config.properties)
echo $ADMIN_PWD > pwd.txt
chmod 400 pwd.txt

EXT_CLASSPATH=$CLASSPATH

cd "$TOOLS_HOME"

# Check if ssoadm has been setup yet. Assumes openam install dir is on /root/openam
if [ ! -d openam ]; then
    echo "[i] Setting up ssoadm for the first time"
    ./setup --path /root/openam --debug $TOOLS_HOME/debug \
	   --log $TOOLS_HOME/log --acceptLicense
fi

echo "[i] processing with ssoadm set-attr-defs for domain=${DOMAIN}"
/opt/ssoadm/openam/bin/ssoadm set-attr-defs \
	-u amadmin -f /opt/ssoadm/pwd.txt \
	-s iPlanetAMPlatformService -t global \
	-a iplanet-am-platform-cookie-domains=.$DOMAIN
echo '[i] Cookie domain update done.'

#declare -a OAM_IN_AGENTS=$OAM_IN_AGENTS
#for i in "${!OAM_IN_AGENTS[@]}"
#do
#    echo "[i] $i=>${OAM_IN_AGENTS[i]}"
#    service=${OAM_IN_AGENTS[i]}
#    service=`echo "$service" | tr '[:upper:]' '[:lower:]'`
#    if [ -f "/opt/ssoadm/agent-config.tmpl.$service" ]; then
#        echo "[i] Found template agent-config.tmpl.$service"
#        cp /opt/ssoadm/agent-config.tmpl.$service /opt/ssoadm/agent-config
#    else
#        echo "[i] Using default template"
#        cp /opt/ssoadm/agent-config.tmpl /opt/ssoadm/agent-config
#    fi
#
#    sed -i "s/{APP}/${service}/g"  /opt/ssoadm/agent-config
#    sed -i "s/{DOMAIN}/$DOMAIN/g" /opt/ssoadm/agent-config
#    $TOOLS_HOME/openam/bin/ssoadm create-agent --realm "/" \
#	--adminid amadmin --password-file $TOOLS_HOME/pwd.txt \
#	--agenttype WebAgent  --agentname WebAgent$i  \
#	--datafile $TOOLS_HOME/agent-config
#    echo "[i] Create Agent_$i for ${OAM_IN_AGENTS[i]} done."
#done

#Update embedded datastore
echo '[i] processing with: ssoadm update-datastore'
$TOOLS_HOME/openam/bin/ssoadm update-datastore \
	--realm "/"  --adminid amadmin --password-file $TOOLS_HOME/pwd.txt \
	--name embedded --datafile $TOOLS_HOME/embedded_datastore_update.properties
echo '[i] Update embedded datastore done.' 

#Create Authentication Instance with name Certs from type Cert
echo "[i] processing with: ssoadm create-auth-instance to support certs driven login"
$TOOLS_HOME/openam/bin/ssoadm create-auth-instance \
	--realm "/"  --adminid amadmin --password-file $TOOLS_HOME/pwd.txt \
	--authtype Cert --name Certs 
echo '[i] Create Authentication Instance Certs done.' 


#CREATE policy
echo '[i] Try to log in and get the TOKEN.....' 

SERVER_URL=$(awk -F "=" '/SERVER_URL/ {print $2}' openam-config.properties)
SERVER_URL=${SERVER_URL// } #remove leading spaces
SERVER_URL=${SERVER_URL%% } #remove trailing spaces
SERVER_URL=${SERVER_URL//$'\n'/} # Remove all newlines.
SERVER_URL=${SERVER_URL%$'\n'}   # Remove a trailing newline.
ADMIN_PWD=$(awk -F "=" '/ADMIN_PWD/ {print $2}' openam-config.properties)
ADMIN_PWD=${ADMIN_PWD//$'\n'/} # Remove all newlines.
ADMIN_PWD=${ADMIN_PWD%$'\n'}   # Remove a trailing newline.
DEPLOYMENT_URI=$(awk -F "=" '/DEPLOYMENT_URI/ {print $2}' openam-config.properties)
DEPLOYMENT_URI=${DEPLOYMENT_URI// } #remove leading spaces
DEPLOYMENT_URI=${DEPLOYMENT_URI%% } #remove trailing spaces
DEPLOYMENT_URI=${DEPLOYMENT_URI//$'\n'/} # Remove all newlines.
DEPLOYMENT_URI=${DEPLOYMENT_URI%$'\n'}   # Remove a trailing newline.
TOKEN=$(curl --request POST --header "X-OpenAM-Username: amadmin" \
	--header "X-OpenAM-Password:  $ADMIN_PWD " \
	--header "Content-Type: application/json" \
	--data "{}" $SERVER_URL$DEPLOYMENT_URI/json/authenticate | \
	python -c "import sys, json; print json.load(sys.stdin)['tokenId']")

curl --header "iPlanetDirectoryPro: $TOKEN" \
     --header "Content-Type: application/json" \
     --data @policy.json $SERVER_URL$DEPLOYMENT_URI/json/policies?_action=create

echo''
echo "[i] obtain uuid of URL resource type"
RESTYPE_UUID=$(curl --header "iPlanetDirectoryPro: $TOKEN" \
      --header "Content-Type: application/json" \
      --get --data-urlencode '_queryFilter=name eq "URL"' \
      --data-urlencode '_fields=uuid' \
      "$SERVER_URL$DEPLOYMENT_URI/json/resourcetypes" \
      | python -c "import sys,json; print json.load(sys.stdin)['result'][0]['uuid']")

echo "[d] got uuid: $RESTYPE_UUID"

# insert uuid into policy set template for rt & imq
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policyset-rt.json
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policy-rt.json
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policyset-imq.json
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policy-imq.json


echo "[i] create iauthorization policy set for RT"
curl --header "iPlanetDirectoryPro: ${TOKEN}" \
     --header "Content-Type: application/json" \
     --data @policyset-rt.json \
     "${SERVER_URL}${DEPLOYMENT_URI}/json/applications?_action=create"

echo ''
echo "[i] create authorization policy for RT"
curl --header "iPlanetDirectoryPro: ${TOKEN}" \
     --header "Content-Type: application/json" \
     --data @policy-rt.json \
     "${SERVER_URL}${DEPLOYMENT_URI}/json/policies?_action=create"

echo ''
echo "[i] create authorization policy set for IntelMQ"
curl --header "iPlanetDirectoryPro: ${TOKEN}" \
     --header "Content-Type: application/json" \
     --data @policyset-imq.json \
     "${SERVER_URL}${DEPLOYMENT_URI}/json/applications?_action=create"

echo ''
echo "[i] create authorization policy for IntelMQ"
curl --header "iPlanetDirectoryPro: ${TOKEN}" \
     --header "Content-Type: application/json" \
     --data @policy-imq.json \
     "${SERVER_URL}${DEPLOYMENT_URI}/json/policies?_action=create"

echo ''
echo '[i] Create policy done.'

#create user and groups
echo "[i] creating groups, users and assign the users to corresponding groups"

$TOOLS_HOME/openam/bin/ssoadm do-batch --batchfile $TOOLS_HOME/post.batch --adminid amadmin --password-file $TOOLS_HOME/pwd.txt

echo '[i] Create Users and Groups done.'

#Update Authentication Instance Cert 
$TOOLS_HOME/openam/bin/ssoadm update-auth-instance --realm "/"  \
	--adminid amadmin --password-file $TOOLS_HOME/pwd.txt \
	--datafile Cert.properties --name Certs 
echo '[i] Update Authentication Instance Certs done.' 

echo ''
echo '[i] !!!!Post Configuration complete!!!!'

