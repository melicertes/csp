#!/bin/bash

# generates internal certificate and CA
# if found, it will remove it and recreate
# happens in /opt/csp/internalCerts
function generateInternal() {
        echo "CSPNAME   = $CSPNAME"
	    echo "CSPDOMAIN = $CSPDOMAIN"
        mkdir -p /etc/cspinst

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
        docker run --rm -v "$(pwd)":/mnt --workdir /mnt thanosa75/alpine-jdk8:slim sh -c "$CMD"
        verifyRetCode $?

        mkdir -p /opt/csp/apache2/ssl/ca
        mkdir -p /opt/csp/apache2/ssl/server

        cp internalCA.crt /opt/csp/apache2/ssl/ca/common-internal-ca.crt
        cp internal.server.p12 /opt/csp/apache2/ssl/server/csp-internal.p12
        cp internal.server.jks /opt/csp/apache2/ssl/server/csp-internal.jks
        cp internal.server.crt /opt/csp/apache2/ssl/server/csp-internal.crt
        cp internal.server.key /opt/csp/apache2/ssl/server/csp-internal.key

        cd $CWDIR

        if [[ $SUMCODES -gt 0 ]]; then
                echo "Process has failed. Oups"
               return 200
        else
                touch .stateinca
                echo "Process completed successfully, internal certificates generated"
		return 0
        fi

}

function verifyRetCode() {

    if [[ $1 != 0 ]]; then
        echo "an OpenSSL process has returned a non-zero code. This is probably a failure"
    fi
    echo "Returned code $1"
    SUMCODES=$(( $SUMCODES + $1 ))
    return 0


}


generateInternal

