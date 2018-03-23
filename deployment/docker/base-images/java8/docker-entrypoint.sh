#!/bin/ash

INTERNAL_CA="/opt/ssl/ca/common-internal-ca.crt"
EXTERNAL_CA="/opt/ssl/ca/common-external-ca.crt"

if [ -f "$INTERNAL_CA" ]; then
    echo
    echo 'Importing Internal CA certificate in Java Truststore.'
    echo
    $JAVA_HOME/bin/keytool -importcert -file $INTERNAL_CA  -alias CspInCaAuth -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit
fi

if [  -f "$EXTERNAL_CA" ]; then
    echo
    echo 'Importing External CA certificate in Java Truststore.'
    echo
    $JAVA_HOME/bin/keytool -importcert -file $EXTERNAL_CA -alias CspExCaAuth -keystore $JAVA_HOME/jre/lib/security/cacerts -noprompt -storepass changeit
fi

exec "$@"