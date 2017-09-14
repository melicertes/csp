#!/bin/bash

function processExternalCerts() {
	echo "CSPNAME   = $CSPNAME"
	echo "CSPDOMAIN = $CSPDOMAIN"
	echo "TMPDIR    = $TMPDIR"


        rm -fr /opt/csp/externalCerts .statefiles
        mkdir -p /opt/csp/externalCerts &>/dev/null
        mv $TMPDIR/ca-bundle.crt /opt/csp/externalCerts &>/dev/null
        mv $TMPDIR/sslprivate.key /opt/csp/externalCerts &>/dev/null
        mv $TMPDIR/sslpublic.crt /opt/csp/externalCerts &>/dev/null

        local counted=$(ls -l /opt/csp/externalCerts |grep -v total | wc -l)
        if [[ $counted == 3 ]]; then
                echo "Files copied correctly"
        else
                echo "Files not copied correctly or not found in /tmp folder with expected names"
                echo "Files for external certificates not found: `ls -lrt /tmp`"
                return 1
        fi


        #init error counter
        SUMCODES=0

        # create symlinks here for external certificates
        cp /opt/csp/externalCerts/ca-bundle.crt /opt/csp/apache2/ssl/ca/common-external-ca.crt
        cp /opt/csp/externalCerts/sslpublic.crt /opt/csp/apache2/ssl/server/csp-external.crt
        cp /opt/csp/externalCerts/sslprivate.key /opt/csp/apache2/ssl/server/csp-external.key

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
                echo "Process has failed. Oups"
                return 1
        else
                touch .statefiles
                echo "Process completed successfully, external certificates generated for JKS"
        fi
        return 0
}

processExternalCerts
