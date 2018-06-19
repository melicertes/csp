## vim:ts=4:sw=4:tw=200:nu:ai:nowrap:filetype=sh:
##
## application library for bashinator example application
##
## Created by Wolfram Schlich <wschlich@gentoo.org>
## Licensed under the GNU GPLv3
## Web: http://www.bashinator.org/
## Code: https://github.com/wschlich/bashinator/
##

##
## REQUIRED PROGRAMS
## =================
## - rm
## - mkdir
## - ls
##

##
## application initialization function
## (command line argument parsing and validation etc.)
##

function __init() {

	## -- BEGIN YOUR OWN APPLICATION INITIALIZATION CODE HERE --

	DOCKERIMG="csp_docker.tgz"
	ORIGWD="$(pwd)"
	cd /etc/cspinst 

	## parse command line options
	while getopts ':h' opt; do
		case "${opt}" in
			## option a
			h)
				HELP=1
				;;
			## option b
			s)
				STAGE="${OPTARG}"
				;;
			## quiet operation
			q)
				declare -i __MsgQuiet=1
				;;
			## option without a required argument
			:)
				__die 2 "option -${OPTARG} requires an argument" # TODO FIXME: switch to __msg err
				;;
			## unknown option
			\?)
				__die 2 "unknown option -${OPTARG}" # TODO FIXME: switch to __msg err
				;;
			## this should never happen
			*)
				__die 2 "there's an error in the matrix!" # TODO FIXME: switch to __msg err
				;;
		esac
		__msg debug "command line argument: -${opt}${OPTARG:+ '${OPTARG}'}"
	done
	## check if command line options were given at all
	#if [[ ${OPTIND} == 1 ]]; then
	#	__die 2 "no command line option specified" # TODO FIXME: switch to __msg err
	#fi
	## shift off options + arguments
	let OPTIND--; shift ${OPTIND}; unset OPTIND
	args="${@}"
	set --

	return 0 # success

	## -- END YOUR OWN APPLICATION INITIALIZATION CODE HERE --

}

##
## application main function
##

function __main() {

	## -- BEGIN YOUR OWN APPLICATION MAIN CODE HERE --

#	local i
#	for i in debug info notice warning err crit alert emerg; do
#		__msg ${i} "this is a ${i} test"
#	done

#### autoupdate

	local autoupdate=$(curl --head http://central.preprod.melicertes.eu/repo-loads/cspinst/autoupdate |grep ETag |tr -d '\"' |awk '{ print $2}' | awk -F'-' '{ print $1 }')

	if [ "$autoupdate" == "0" ]; then
	    __msg debug "No update is detected. Proceeding..."
	else 
	    __msg info "UPDATE detected; will download"
	    downloadUpdateAndExecute
	fi


	#if [[ ${HELP} == 1 ]]; then
		verifyInstall
		showMenu
		return 0 # success
        #fi


	return 0 # success

	## -- END YOUR OWN APPLICATION MAIN CODE HERE --

}

##
## application worker functions
##


function verifyInstall() {
	VER_DOCKER=`docker info|grep Server|awk '{print $3}' 2>/dev/null`
	__msg info "Docker installation found, version is $VER_DOCKER"
	VER_SSL=`openssl version`
	__msg info "OpenSSL configured is $VER_SSL"
	if [ -z "$VER_DOCKER" ]; then
           __die 2 "Docker is required and not installed"
	fi
	if [ -z "$VER_SSL" ]; then
	   _die 2 "OpenSSL / LibreSSL is required and not installed"
	fi
	__msg info "The following process verifies internet connectivity"
	__msg info "It may take up to 5 minutes to complete"
	__msg info "Trying to detect external IP address and PTR"
	EXT_IP=`drill myip.opendns.com @resolver1.opendns.com|grep IN|grep -v ";;"| awk '{print $5}'`
        EXT_NAME=`drill -x $EXT_IP @8.8.8.8 |grep -v ";;" | grep PTR | awk '{print $5}'`
	if [ -z "$EXT_NAME" ]; then
	   _msg crit "Network not configured correctly - $EXT_IP does not have a PTR entry!"
        fi
	local default_iface=$(awk '$2 == 00000000 { print $1 }' /proc/net/route)
        INT_IP=$(ip addr show dev "$default_iface" | awk '$1 ~ /^inet/ { sub("/.*", "", $2); print $2 }' | head -1)
	INT_MAC=$(ip addr show dev "$default_iface" |grep ether| awk '{ print $2 }')
	if [ "`whoami`" == "root" ]; then
		__msg info "root user detected"
	else
		__msg crit "this script expects root privileges"
		__die 2 "root user not found"
	fi 


	## make necessary directories
	mkdir -p /opt/csp/apache2/ssl/ /opt/csp/apache2/ssl/ca /opt/csp/apache2/ssl/server


	return 0
}
function showHeaderMenu() {
	clear
	echo "CSP Node installation (CSP ALPHA) - Part 1"
	echo ""
	echo "Welcome to the CSP installation script. See below for information and prompts"
	echo ""
	echo "Detected: Docker $VER_DOCKER, SSL $VER_SSL"
	echo "Internal IP: $INT_IP (MAC addr: $INT_MAC)"
	echo "External IP: $EXT_IP ($EXT_NAME)"
	echo ""
	echo "Required for installation: "
        echo " * Access to the internet (check internal/external IP detected)"
	echo " * A valid CSP Node name: $CSPNAME (see below for options)"
	echo " * A valid CSP FQDN that correctly resolves: $CSPNAME.$CSPDOMAIN"
 	echo " * External certificates as defined by the procedure"

}
function showMenu() {

	## here we source previously saved configuration
	[[ -e ".statedns" ]] && source .statedns

	showHeaderMenu

	local loop=true
	
	while $loop; do
		showMenuPre 
		local answer=""
		read -e -i "$answer" -p "Choose an option: " input
		answer="${input:-$answer}"
		case ${answer} in
			1)
				configureNameDomain
				;;
			2) 
				uploadCerts
				;;
			3) 
				generateInternal
				;;
			4) 		
				verifyAndContinue
				local ret=$?
				if [[ $ret == 0 ]]; then
					loop=false
				fi
				;;
			0) 
				showHeaderMenu
				;;
			exit) 	
				return 0
				;;
			reboot)
				__msg warn "Reboot requested from the user. Rebooting now"
				sleep 1
				reboot
				sleep 600
				;;
			*)
				__msg err "invalid response, please try again"
				;;
		esac				

	done

	continueInstall	
	
	return 0
}

function verifyAndContinue() {
	__msg info "Verifying that DNS and CAs are present -> $DNS_OK $CA_OK"
	if [ -z "$DNS_OK" ]; then
		__msg err "DNS configuration not complete."
		return 1
	elif [ -z "$CA_OK" ]; then
		__msg err "CA certificates not uploaded or not correct."
		return 1
	elif [ -z "$INCA_OK" ]; then
                __msg err "Internal CA certificates not correct or not generated yet."
	fi
	return 0
}
function configureNameDomain() {
	rm -f .stateok
	local input=""
	read -e -i "$CSPNAME" -p "Enter the name of this CSP: " input
	CSPNAME="${input:-$CSPNAME}"
	local input=""
	read -e -i "$CSPDOMAIN" -p "Enter the domain: " input
	CSPDOMAIN="${input:-$CSPDOMAIN}"
	local fqdn="$CSPNAME.$CSPDOMAIN"
	echo "FQDN defined: $fqdn"
	echo "This FQDN should match the external address we have detected, now checking..."
        local detected=`drill $fqdn @8.8.8.8 |grep -v ";;" | grep IN | awk '{print $5}'`	
	if [ "$detected" == "$EXT_IP" ]; then
		echo "Detected DNS lookup ($detected) matches own external IP address ($EXT_IP) for $fqdn"
		__msg info "DNS lookup is correct - $detected $fqdn"
		echo "CSPNAME=$CSPNAME" > .statedns
		echo "CSPDOMAIN=$CSPDOMAIN" >> .statedns
		touch .stateok
	else
		echo "DNS lookup failed. Please configure your DNS correctly"
		__msg err "DNS Lookup failed for $fqdn : returned $detected instead of $EXT_IP" 	
	fi

}

function editSMTP() {
	if [ -f .statesmtp ]; then
	        # reload as variables
	        source .statesmtp
	fi

	echo "Found mail configuration:"
	cat .statesmtp
	read -e -i "no" -p "Do you want to (re)configure mail server properties? (yes/no) " REPLY
	
	if [ "$REPLY" == "yes" ]; then
	  echo ""
	  echo "Configure mail server properties:"
	  read -e -p "host: " MAIL_HOST
	  read -e -p "port: " MAIL_PORT
	  read -e -p "username: " MAIL_USERNAME
	  read -e -p "password: " MAIL_PASSWORD
	  echo ""
          #saving to state
	  echo "MAIL_HOST=${MAIL_HOST}" > .statesmtp
	  echo "MAIL_PORT=${MAIL_PORT}" >> .statesmtp
	  echo "MAIL_USERNAME=${MAIL_USERNAME}" >> .statesmtp
	  echo "MAIL_PASSWORD=${MAIL_PASSWORD}" >> .statesmtp
	fi



	
	return 0;
}
function applySMTP() {

    echo "SMTP is configured as: "
    cat .statesmtp
    source .statesmtp
    echo "Press [ENTER] to apply properties to configuration"
    read 
 
    local ENVJSON=$(find /etc/cspinst/install -name "env.json")
    local conf=$(fgrep XXXMAILHOSTXXX ${ENVJSON} | wc -l)
    if [[ $conf -lt 1 ]]; then
	echo "Configuration has been altered and cannot be re-applied. The setup needs to be restarted"
	__die 200 "Problem with env.json configuration and SMTP properties"
    fi

    sed -i.bak11 "s#XXXMAILHOSTXXX#${MAIL_HOST}#g" "${ENVJSON}"
    sed -i.bak12 "s#XXXMAILPORTXXX#${MAIL_PORT}#g" "${ENVJSON}"
    sed -i.bak13 "s#XXXMAILUSERNAMEXXX#${MAIL_USERNAME}#g" "${ENVJSON}"
    sed -i.bak14 "s#XXXMAILPASSWORDXXX#${MAIL_PASSWORD}#g" "${ENVJSON}"

    return 0
}
function showMenuPre() {
	setVarsPreCheck

	echo ""
	echo "Options to continue:"
	echo "1 - configure Name and Domain          $DNS_OK"
 	echo "2 - upload certificates                $CA_OK"
 	echo "3 - generate internal certificates     $INCA_OK"
	echo "4 - continue installation (both steps above should be OK)"
}

function setVarsPreCheck() {
	unset DNS_OK
	unset CA_OK
	unset INCA_OK
	if [ -e .statedns ]; then
		DNS_OK="[OK]"
	fi
	if [ -e .statefiles ]; then
		CA_OK="[OK]"
	fi
	if [ -e .stateinca ]; then
		INCA_OK="[OK]"
	fi
}

function uploadCerts() {
	rm -f .statefiles
	local genpw=$(openssl rand -base64 12)
	deluser installcspuser &>/dev/null
	adduser -h /tmp -s /bin/sh -D installcspuser &>/dev/null
	echo "installcspuser:$genpw" | chpasswd -c sha512 &>/dev/null
	echo "User 'installcspuser' has been activated with password '$genpw'"
	echo "Please copy the certificates using the following names to the /tmp folder."
	echo "The system will automatically pick them up and use them:"
	echo ""
	echo "SSL certificate     :   /tmp/$CSPNAME.$CSPDOMAIN.crt"
	echo "SSL certificate Key :   /tmp/$CSPNAME.$CSPDOMAIN.key"
	echo "SSL CA bundle       :   /tmp/ca-bundle.crt"

	local answer="nr"
        read -e -i "$answer" -p "when files have been uploaded, respond with 'ready': " input
        answer="${input:-$answer}"

	#we force delete the user here
        deluser installcspuser &>/dev/null

	if [ "$answer" == "ready" ]; then
		verifyFiles
		ret=$?
		if [[ $ret == 0 ]]; then
			echo "Files verified. Please wait"
			sleep 5
		else
			sleep 2
		fi
	else
		echo "Configuration not complete. Back to main menu"
	fi
	return 0	
}

function verifyFiles() {
	rm -fr /opt/csp/externalCerts .statefiles
	mkdir -p /opt/csp/externalCerts &>/dev/null
	mv /tmp/$CSPNAME.$CSPDOMAIN.* /opt/csp/externalCerts &>/dev/null
	mv /tmp/ca-bundle.crt /opt/csp/externalCerts &>/dev/null

	local counted=$(ls -l /opt/csp/externalCerts |grep -v total | wc -l)
	if [[ $counted == 3 ]]; then
		echo "Files copied correctly"
	else
		echo "Files not copied correctly or not found in /tmp folder with expected names"
		__msg err "Files for external certificates not found: `ls -lrt /tmp`"
		return 1
	fi


	#init error counter
	SUMCODES=0

	# create symlinks here for external certificates
	cp /opt/csp/externalCerts/ca-bundle.crt /opt/csp/apache2/ssl/ca/common-external-ca.crt
	cp /opt/csp/externalCerts/$CSPNAME.$CSPDOMAIN.crt /opt/csp/apache2/ssl/server/csp-external.crt
	cp /opt/csp/externalCerts/$CSPNAME.$CSPDOMAIN.key /opt/csp/apache2/ssl/server/csp-external.key

	CWDIR="$(pwd)"
	cd /opt/csp/externalCerts/
	
	openssl pkcs12 -export -clcerts -in $CSPNAME.$CSPDOMAIN.crt \
		-inkey $CSPNAME.$CSPDOMAIN.key -passout pass:changeme -out $CSPNAME.$CSPDOMAIN.p12

      	local CMD="keytool -importkeystore -srcstorepass changeme -srckeystore $CSPNAME.$CSPDOMAIN.p12 \
		-srcstoretype pkcs12 -destkeystore $CSPNAME.$CSPDOMAIN.jks -deststoretype JKS \
		-noprompt -storepass changeme"
	docker run --rm -v "$(pwd)":/mnt --workdir /mnt frolvlad/alpine-oraclejdk8:slim sh -c "$CMD" 
	verifyRetCode $?
	### fixing jitsi keystore
	openssl pkcs12 -export -clcerts -in $CSPNAME.$CSPDOMAIN.crt \
		-inkey $CSPNAME.$CSPDOMAIN.key -passout pass:changeit -out $CSPNAME.$CSPDOMAIN-jitsi.p12
      	local CMD="keytool -importkeystore -srcstorepass changeit -srckeystore $CSPNAME.$CSPDOMAIN-jitsi.p12 \
		-srcstoretype pkcs12 -destkeystore $CSPNAME.$CSPDOMAIN-jitsi.jks -deststoretype JKS \
		-noprompt -storepass changeit"
	docker run --rm -v "$(pwd)":/mnt --workdir /mnt frolvlad/alpine-oraclejdk8:slim sh -c "$CMD" 
	verifyRetCode $?

	cp /opt/csp/externalCerts/$CSPNAME.$CSPDOMAIN-jitsi.jks /opt/csp/apache2/ssl/server/csp-external-jitsi.jks
	cp /opt/csp/externalCerts/$CSPNAME.$CSPDOMAIN.p12 /opt/csp/apache2/ssl/server/csp-external.p12
	cp /opt/csp/externalCerts/$CSPNAME.$CSPDOMAIN.jks /opt/csp/apache2/ssl/server/csp-external.jks
	
	cd "$CWDIR"

	if [[ $SUMCODES -gt 0 ]]; then
		__msg crit "Process has failed. Oups"
		return 1
	else
		touch .statefiles
		__msg info "Process completed successfully, external certificates generated for JKS"
	fi
	return 0
}


# generates internal certificate and CA
# if found, it will remove it and recreate
# happens in /opt/csp/internalCerts
function generateInternal() {

        [[ -e "/etc/cspinst/.seedkey" ]] && local seed=$(cat /etc/cspinst/.seedkey)

	if [[ -z $seed ]]; then
		echo "Generating seed..."
		local seed=$(openssl rand -base64 36)
		echo "$seed" >/etc/cspinst/.seedkey
		chmod 600 /etc/cspinst/.seedkey
	fi

	echo "Using $seed for calculations..."

	#init error counter
	SUMCODES=0
	rm -fr /opt/csp/internalCerts .stateint
        mkdir -p /opt/csp/internalCerts &>/dev/null
	local CWDIR=`pwd`
	cd /opt/csp/internalCerts

	# generate root CA key
	openssl genrsa -passout file:/etc/cspinst/.seedkey -aes256 -out internalCA.key 4096
	verifyRetCode $?
	openssl rsa -passin file:/etc/cspinst/.seedkey -in internalCA.key -out server.key
	verifyRetCode $?

	mv -f server.key internalCA.key
	

	openssl req -new -x509  -days 3650 -key internalCA.key -out internalCA.crt \
		-subj "/C=EU/ST=Belgium/L=Brussels/O=CSP Project/OU=$CSPNAME Internal CA/CN=local.$CSPNAME.$CSPDOMAIN"
	verifyRetCode $?

	#generate the server key
	openssl genrsa -passout file:/etc/cspinst/.seedkey -aes256 -out internal.server.key 4096
	verifyRetCode $?

	openssl rsa -passin file:/etc/cspinst/.seedkey -in internal.server.key -out server.key
        verifyRetCode $?

	mv -f server.key internal.server.key

	openssl req -new -key internal.server.key -out internal.server.csr \
  		-subj "/C=EU/ST=Belgium/L=Brussels/O=CSP Project/OU=Local Node $CSPNAME/CN=*.local.$CSPNAME.$CSPDOMAIN"
	verifyRetCode $?

	openssl x509 -req -days 3650 -in internal.server.csr -CAcreateserial -CAserial /etc/cspinst/.ca.seq \
	        -CA internalCA.crt -CAkey internalCA.key -out internal.server.crt
	verifyRetCode $?

	openssl verify -purpose sslserver -CAfile internalCA.crt internal.server.crt
	verifyRetCode $?

	openssl verify -purpose sslclient -CAfile internalCA.crt internal.server.crt
	verifyRetCode $?

	openssl pkcs12 -export -clcerts -in internal.server.crt -passout pass:changeme  \
		-inkey internal.server.key -out internal.server.p12
	verifyRetCode $?

	## link to correct locations
	
      	local CMD="keytool -importkeystore -srcstorepass changeme -srckeystore internal.server.p12 -srcstoretype pkcs12 -destkeystore internal.server.jks -deststoretype JKS -noprompt -storepass changeme"
	docker run --rm -v "$(pwd)":/mnt --workdir /mnt frolvlad/alpine-oraclejdk8:slim sh -c "$CMD" 
	verifyRetCode $?
	
	cp internalCA.crt /opt/csp/apache2/ssl/ca/common-internal-ca.crt
	cp internal.server.p12 /opt/csp/apache2/ssl/server/csp-internal.p12
	cp internal.server.jks /opt/csp/apache2/ssl/server/csp-internal.jks
	cp internal.server.crt /opt/csp/apache2/ssl/server/csp-internal.crt
	cp internal.server.key /opt/csp/apache2/ssl/server/csp-internal.key

	cd $CWDIR

	if [[ $SUMCODES -gt 0 ]]; then
		__msg crit "Process has failed. Oups"
	else
		touch .stateinca
		__msg info "Process completed successfully, internal certificates generated"
	fi

}

function verifyRetCode() {
	if [[ $1 != 0 ]]; then
		__msg crit "an OpenSSL process has returned a non-zero code. This is probably a failure"
	fi
	__msg debug "Returned code $1"
	SUMCODES=$(( $SUMCODES + $1 ))
	return 0
}

function continueInstall() {

	downloadCSPImage
	echo ""
	echo ""

	editSMTP
	applySMTP

        local loop=true
        while $loop; do
		verifyImagesBuild
		showDockerMenu
                local answer=""
                read -e -i "$answer" -p "Choose an option: " input
                answer="${input:-$answer}"
                case ${answer} in
                        1)
                                buildBase
                                ;;
                        2)
                                launch
                                ;;
                        exit)
                                loop=false
                                ;;
                        *)
                                __msg err "invalid response, please try again"
                                ;;
                esac

        done
	return 0
}

function downloadCSPImage() {
	echo "Image for CSP is being retrieved."

	if [ ! -f /etc/cspinst/$DOCKERIMG ]; then
		echo "Fail - image not found"
		__msg crit "Image for docker descriptions not found"
		__die 200 "Image $DOCKERIMG not found"
		return 1
	fi
	local CWDIR="$(pwd)"
	mkdir -p /etc/cspinst/install
	cd /etc/cspinst/install
	tar xvfz /etc/cspinst/$DOCKERIMG &>/dev/null
	echo "Image #1 extracted. "
	echo "Downloading image #2...."
	curl --progress-bar -o docker/applications/jitsi/directories.zip http://central.preprod.melicertes.eu/repo-loads/directories.zip
	local ret=$?
	echo "Downloading image #3...."
	curl --progress-bar -o docker/applications/jitsi/vc-bridge-admin-app.jar http://central.preprod.melicertes.eu/repo-loads/vc-bridge-admin-app.jar
	local r=$(( $? ))
	curl --progress-bar -o /opt/csp/il/integration-tests-3.8.0-SNAPSHOT.jar http://central.preprod.melicertes.eu/repo-loads/integration-layer/integration-tests-3.8.0-SNAPSHOT.jar
	r=$(( $r + $? ))
	curl --progress-bar -o /opt/csp/il/il-server-0.0.1-ALPHA.jar http://central.preprod.melicertes.eu/repo-loads/integration-layer/il-server-0.0.1-ALPHA.jar
	r=$(( $r + $? ))
	local ret2=$(( $r + ret ))
        echo "Downloading image #4...."
	#curl --progress-bar -o docker/applications/postgres/migrations/tcdb.sql http://central.preprod.melicertes.eu/repo-loads/tcdb.sql
	curl --progress-bar -o docker/applications/postgres/migrations/tcdb.sql http://central.preprod.melicertes.eu/repo-loads/tcdb.clean.sql
	local ret3=$(( $? + ret2 ))



	if [[ ret3 -ne 0 ]]; then
	    __msg crit "Images required did not complete download - this process cannot continue until internet access is restored."
	    sleep 15
	    __die 2 "Exiting due to previous error"
	fi

	



	__msg info "Images for docker configuration is extracted"
	cd $CWDIR
	return 0
}



function buildBase() {
	BUILD_ALL=`find /etc/cspinst -name "build-all.sh"`

	if [ ! -f $BUILD_ALL ]; then
		echo "Fail - start script not found"
                __msg crit "start script build-all.sh not found"
                return 1
        fi

	cd /etc/cspinst/install/docker
	echo "Building images using $BUILD_ALL....(`pwd`)"
	sleep 5
	$BUILD_ALL
	echo "images built" 
	verifyImagesBuild
	cd /etc/cspinst
	return 0
}
function verifyImagesBuild(){
	RCODE=0	
	verifyImage csp-tomcat8 
	verifyImage csp-python3
	verifyImage csp-python27
	verifyImage csp-postgres
	verifyImage csp-java8
	verifyImage csp-apache
	verifyImage csp-jitsiopenfire
	verifyImage csp-openam
	verifyImage csp-owncloud
	if [[ $RCODE -gt 0 ]]; then
		IMGBUILD="[ERROR]"
		__msg crit "Found $RCODE problematic images. Please retry fixing internet access. If problem persists, call support"
	else
		IMGBUILD="[OK]"
	fi	
	return 0;
}
function verifyImage() {
	local IMG=`docker images |grep "$1"|awk '{print $3}'`
	if [ -z ${IMG} ]; then
		__msg err "Image $1 not found. Retry creating the base images"
		RCODE=$(( $RCODE + 1 ))
		return 1
	else
		__msg info "Image $1 found with hash $IMG --> OK"
	fi
	return 0
}


function launch() {
	if [ -z "$IMGBUILD" ]; then
		__msg info "Images not build or rebuilding is necessary. Please select 1st option"
		return 1
	fi

	# make sure we're in the right directory
	cd /etc/cspinst 
	### setup env.json file
	echo $(pwd)
	local ENVJSON=$(find install -name "env.json")
	local J2ENV=$(find install -name ".env.j2")
	local SITESC=$(find install -name "csp-sites.conf.j2")


	echo "$(fgrep XXXDOMAINXXX $ENVJSON)"
	echo "environment found: $ENVJSON $J2ENV $SITESC"
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
   	    __msg warn "J2 not found !"
	    __die 2 "j2 not found in location, was in `which j2`"
	fi
	
	### generate env file
	$J2 $J2ENV $ENVJSON > ~/.env	
	## generate sites conf
	$J2 $SITESC $ENVJSON  > ~/csp-sites.conf
	
	### create docker network
	local DN=$(docker network ls |grep local.$CSPNAME.$CSPDOMAIN|wc -l)
	if [ "$DN" == "0" ]; then
		echo "Creating docker network local.$CSPNAME.$CSPDOMAIN"
		docker network create local.$CSPNAME.$CSPDOMAIN
	fi
	echo "Docker network (internal) should be local.$CSPNAME.$CSPDOMAIN"
				
	local composeScripts=(
		"install/docker/applications/postgres/docker-compose.yml"
		"install/docker/applications/owncloud/docker-compose.yml"
		"install/docker/applications/elasticsearch/docker-compose.yml"
		"install/docker/applications/kibana/docker-compose.yml"
		"install/docker/applications/jitsi/docker-compose.yml"
		"install/docker/applications/integrationlayer/docker-compose.yml"
		"install/docker/applications/trustcircles/docker-compose.yml"
		"install/docker/applications/mocknode/mockadapter.yml"
	)	

	# get length of an array
	local arraylength=${#composeScripts[@]}
	for (( i=1; i<${arraylength}+1; i++ ));
	do
  		echo $i " / " ${arraylength} " : " ${composeScripts[$i-1]}
		prepareEnv ${composeScripts[$i-1]} 
		composeExecute ${composeScripts[$i-1]} create
		composeExecute ${composeScripts[$i-1]} start
		echo "waiting ....."
		sleep 15
	done

	# are we sure the directory exists?
	mkdir -p /opt/csp/apache2/csp-sites

	cp ~/csp-sites.conf /opt/csp/apache2/csp-sites
	echo "Site configuration copied"
	ls -l /opt/csp/apache2/csp-sites

	prepareEnv "install/docker/applications/openam/docker-compose.yml"
	prepareEnv "install/docker/applications/apache/docker-compose.yml"

	composeExecute "install/docker/applications/openam/docker-compose.yml" rm -f -s

	restartOpenAM


	return 0
}

function restartOpenAM() {
	# start OpenAM and wait for it to be ready
	composeExecute "install/docker/applications/openam/docker-compose.yml" create
	composeExecute "install/docker/applications/openam/docker-compose.yml" start
	echo "Waiting 5 minutes for OpenAM to complete setup"
        sleep 120
	echo "Waiting, 3 minutes left..."
	sleep 60
	echo "Waiting, 2 minutes left..."
	sleep 60
	echo "Waiting, 1 minutes left..."
	sleep 60


	local serviceWait=$(docker exec -ti csp-oam wget -O /dev/null http://csp-oam:8080/openam/ &>/dev/null ; echo $?)
	while [ $serviceWait -gt 0 ] ; do
	    echo "OpenAM site not ready yet, waiting to become available"
	    sleep 90
	    serviceWait=$(docker exec -ti csp-oam wget -O /dev/null http://csp-oam:8080/openam/ &>/dev/null ; echo $?)
	done

	# start apache and wait for it to be ready
        composeExecute "install/docker/applications/apache/docker-compose.yml" create
        composeExecute "install/docker/applications/apache/docker-compose.yml" start
	echo "Waiting 2 minutes for agent configuration on Apache"
	sleep 120
       
        local waitC=0
	while [ $waitC -lt 10 ] ; do
		composeExecute "install/docker/applications/apache/docker-compose.yml" logs
		echo "last logs of HTTP service, should be up: $waitC of 10"
		echo "" 
		echo ""
		echo ""
		echo ""
		sleep 10
		waitC=$(( $waitC + 1))
	done
	echo "Apache and OpenAM should now have been linked." 
	return 0
}

function composeExecute() {
	local compose="/usr/local/bin/docker-compose"
	local operation="${2:-start}"
	local script="$1"
	local filename="${script##*/}"
	local dir="${script:0:${#script} - ${#filename}}"

	echo "[EXEC] $operation [$dir] ----"

	local WD="$(pwd)"
	cd "$dir"
	$compose -f $filename $operation
	local ret=$?
	cd "$WD"	
	if [[ $ret -ne 0 ]]; then
		echo "Compose start failed!"
		FAILED="Tried $operation on $filename in $dir"$'\n'"$FAILED"
		return 1
	fi
	return 0
}

function prepareEnv() {
	local script="$1"
	local filename="${script##*/}"
	local dir="${script:0:${#script} - ${#filename}}"
	
	echo "[PREP] env to $dir ----------------------"
	cp ~/.env "$dir"
	
	if [ ! -f "$dir/.first-time.ok" ]; then
		local WD="$(pwd)"
		cd "$dir"
		if [ -f "first-time.sh" ]; then
			echo "[PREP] first time run"
			bash ./first-time.sh
			echo "[PREP] done - code was $?"
			touch .first-time.ok
		fi
		cd "$WD"
	fi		
	return 0
}

function showRunningDockerImages() {
	CNT=$(docker ps|grep csp|wc -l)
	if [[ $CNT == 0 ]]; then
		echo "No running CSP processes detected"
		return 0
	fi
	echo ""
	echo "Running images:"
	docker ps | grep csp |  awk '{print $1" - "$2 }'
	echo "Failed images:"
	echo "$FAILED"
	return 0

}

function downloadUpdateAndExecute() {

	# download update file
	curl -o /tmp/update.sh http://central.preprod.melicertes.eu/repo-loads/cspinst/autoupdate.payload
	curl -o /tmp/MD5SUM http://central.preprod.melicertes.eu/repo-loads/cspinst/autoupdate.payload.MD5
	local WD=$(pwd)
	cd /tmp
	md5sum -c MD5SUM
	local ret=$?
	if [ $ret -eq 0 ]; then
	   chmod +x update.sh
	   __msg warn "An update has been received and verified"
	   __msg warn "About to execute update"
	   ./update.sh
	   __msg warn "Restarting installer..... - if this fails, execute again 'cspinst.sh' "
	   $0
	   exit 0
	fi
	cd "$WD"
}
function showDockerMenu() {
        echo ""
        echo ""
        echo ""

	echo "CSP Node installation (CSP ALPHA) - Part 2"
        echo ""
        echo "Welcome to the CSP installation script. See below for information and prompts"
        echo ""
        echo "Detected: Docker $VER_DOCKER, SSL $VER_SSL"
        echo "Internal IP: $INT_IP (MAC addr: $INT_MAC)"
        echo "External IP: $EXT_IP ($EXT_NAME)"
	showRunningDockerImages
	echo ""
	echo "Options: "
	echo "1 - Install Docker base images $IMGBUILD"
	echo "2 - (re)Launch system"
	echo "exit - exits the installer"
	echo ""

}
