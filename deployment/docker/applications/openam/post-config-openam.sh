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

# calculate LDAP domain root suffix.
domainarr=$(echo $DOMAIN | tr "." "\n")
for x in $domainarr
do
	ROOT_SUFFIX=$ROOT_SUFFIX"dc="$x","
done
ROOT_SUFFIX=${ROOT_SUFFIX::-1}
if [ -z "$ROOT_SUFFIX" ]; then
    echo "ERROR:LDAP ROOT_SUFFIX is empty,use default suffix. Check the DOMAIN name env."
	ROOT_SUFFIX="dc=openam,dc=cps,dc=com"
fi
echo '[i] update default LDAP domain root suffix=>'$ROOT_SUFFIX
sed -i "s@__ROOT_SUFFIX_TO_BE_REPLACE__@$ROOT_SUFFIX@g" openam-config.properties
sed -i "s@__ROOT_SUFFIX_TO_BE_REPLACE__@$ROOT_SUFFIX@g" policy.json

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

wait_for_openam $SERVER_URL $DEPLOYMENT_URI
java -jar openam-configurator-tool-13.0.0.jar --file openam-config.properties 

echo "[i] writing the password file"
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

echo '[i] copy custom authentication module into classpath of openam'
cp /tmp/openam-auth-cert2-13.0.0.jar $CATALINA_HOME/webapps/openam/WEB-INF/lib/openam-auth-cert2-13.0.0.jar
chmod 444 $CATALINA_HOME/webapps/openam/WEB-INF/lib/openam-auth-cert2-13.0.0.jar


echo "[i] processing with ssoadm set-attr-defs for domain=${DOMAIN}"
echo "set-attr-defs -s iPlanetAMPlatformService -t global -a iplanet-am-platform-cookie-domains=.$DOMAIN" > /tmp/tmp-001.batch
echo '[i] Cookie domain update done.'

#Update embedded datastore
echo '[i] processing with: ssoadm update-datastore'
echo "update-datastore --realm \"/\" --name embedded --datafile $TOOLS_HOME/embedded_datastore_update.properties" >> /tmp/tmp-001.batch
echo '[i] Update embedded datastore done.' 

echo '[i] copy custom authentication module into classpath of openam'
cp /tmp/openam-auth-cert2-13.0.0.jar $CATALINA_HOME/webapps/openam/WEB-INF/lib/openam-auth-cert2-13.0.0.jar
chmod 444 $CATALINA_HOME/webapps/openam/WEB-INF/lib/openam-auth-cert2-13.0.0.jar

echo '[i] performing batch command for tmp-001.batch ...'
$TOOLS_HOME/openam/bin/ssoadm do-batch --batchfile /tmp/tmp-001.batch --adminid amadmin --password-file $TOOLS_HOME/pwd.txt

#Custom Site config
echo "[i] setting up the customized login site" 
sed -i "s@___CSP_DOMAIN___@$DOMAIN@g" $TOOLS_HOME/DataStore.xml
cp $TOOLS_HOME/DataStore.xml $CATALINA_HOME/webapps/openam/config/auth/default/
cp $TOOLS_HOME/DataStore.xml $CATALINA_HOME/webapps/openam/config/auth/default_en/
cp $TOOLS_HOME/login-logo.png $CATALINA_HOME/webapps/openam/XUI/images/
cp $TOOLS_HOME/logo-horizontal.png $CATALINA_HOME/webapps/openam/XUI/images/ 
cp $TOOLS_HOME/ThemeConfiguration.js $CATALINA_HOME/webapps/openam/XUI/config/
cp $TOOLS_HOME/DataStore1.html $CATALINA_HOME/webapps/openam/XUI/templates/openam/authn/
echo '[i] Custom Site config done.' 


#CREATE policy
echo '[i] do authenticate and get the Authentication-TOKEN ...' 
TOKEN=$(curl --request POST --header "X-OpenAM-Username: amadmin" \
	--header "X-OpenAM-Password:  $ADMIN_PWD " \
	--header "Content-Type: application/json" \
	--data "{}" $SERVER_URL$DEPLOYMENT_URI/json/authenticate | \
	python -c "import sys, json; print json.load(sys.stdin)['tokenId']")
if [ -z "$TOKEN" ]; then
	echo "[e] authentication to openam json interface failed!"
	exit 1
fi

echo "[i] creating the ApacheReversProxyPolicy (default) policy"
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

if [ -z "$RESTYPE_UUID" ]; then
	echo "[e] obtain a resource type uuid for policies!"
	exit 1
fi

echo "[i] insert uuid into policy set templates and policies for rt & imq"
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policyset-rt.json
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policy-rt.json
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policyset-imq.json
sed -i "s@__RES_TYPE_ID_TO_BE_INSERTED__@$RESTYPE_UUID@g" policy-imq.json

echo "[i] replace root suffix in policies (user/groups ids)"
# replace root suffix
sed -i "s@__ROOT_SUFFIX_TO_BE_REPLACE__@$ROOT_SUFFIX@g" policy-rt.json
sed -i "s@__ROOT_SUFFIX_TO_BE_REPLACE__@$ROOT_SUFFIX@g" policy-imq.json



echo "[i] create authorization policy set for RT"
curl --header "iPlanetDirectoryPro: ${TOKEN}" \
     --header "Content-Type: application/json" \
     --data @policyset-rt.json \
     "${SERVER_URL}${DEPLOYMENT_URI}/json/applications?_action=create"

echo''
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
echo '[i] Creating policy done.'

#create user and groups
echo "[i] creating groups, users and assign the users to corresponding groups"

$TOOLS_HOME/openam/bin/ssoadm do-batch --batchfile $TOOLS_HOME/post.batch --adminid amadmin --password-file $TOOLS_HOME/pwd.txt

echo '[i] Create Users and Groups done.'


# update the server config -> workaround for cache issue with user data
echo "[i] update the server config configuration"
echo "update-server-cfg -s http://csp-oam:8080/openam -a com.iplanet.am.sdk.caching.enabled=false" > /tmp/tmp-002.batch
echo "update-server-cfg -s http://csp-oam:8080/openam -a com.sun.identity.sm.cache.enabled=true" >> /tmp/tmp-002.batch
echo "[i] update the server config configuration done!"

# update the embedded datastore -> workaround for cache issue with user data
echo "[i] update the datastore"
echo "update-datastore -e \"/\" -m \"embedded\" -a sun-idrepo-ldapv3-config-memberof=" >> /tmp/tmp-002.batch
echo "[i] update the datastore done!"

#Update Authentication Instance Cert 
echo "[i] set up CSP certificate authentication module instance properties"
sed -i "s@___CSP_DOMAIN___@$DOMAIN@g" $TOOLS_HOME/CSP-Cert.properties
sed -i "s@___AMADMIN_PSWD___@$ADMIN_PWD@g" $TOOLS_HOME/CSP-Cert.properties

echo '[i] create empty file Cert2.xml'
touch $CATALINA_HOME/webapps/openam/config/auth/default/Cert2.xml

echo '[i] creating a new service for csp-certificate authentication module'
echo "create-svc --xmlfile /tmp/amAuthCert2.xml" >> /tmp/tmp-002.batch

echo "[i] register new authentication service CSP Certificate"
echo "register-auth-module --authmodule com.fraunhofer.fokus.csp.oam.auth.Cert2" >> /tmp/tmp-002.batch

#Create Authentication Instance with name Certs from type Cert
echo "[i] processing with: ssoadm create-auth-instance to support certs driven login"
echo "create-auth-instance --realm \"/\" --authtype Cert2  --name CSP-Certs" >> /tmp/tmp-002.batch
echo '[i] Create Authentication Instance Certs done.' 

#Updating the authentication chain
echo "[i] processing with: ssoadm update-auth-cfg-entr to updating the authentication chain"
echo "update-auth-cfg-entr --realm \"/\" -m ldapService -a \"CSP-Certs|SUFFICIENT\" \"DataStore|REQUIRED\"" >> /tmp/tmp-002.batch
echo '[i] Updating the authentication chain done.' 

echo '[i] performing command update-auth-instance to update CSP-Certs '
echo "update-auth-instance --realm \"/\" --datafile $TOOLS_HOME/CSP-Cert.properties --name CSP-Certs" >> /tmp/tmp-002.batch
echo '[i] Update Authentication Instance CSP Certs done.' 

echo '[i] performing batch command for tmp-002.batch ...'
$TOOLS_HOME/openam/bin/ssoadm do-batch --batchfile /tmp/tmp-002.batch --adminid amadmin --password-file $TOOLS_HOME/pwd.txt

echo ''
echo '[i] !!!!Post Configuration complete!!!!'

