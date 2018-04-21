#!/usr/bin/env bash

echo "Installing Apache 2 conf files"
mkdir -p /opt/csp/
cp -R ./apache2/ /opt/csp/



#### External
## Create the Server Key, CSR, and Certificate
#
# openssl genrsa -out csp-external.key 1024 -- No passphrase
# openssl req -new -key csp-external.key -out csp-external.csr
#*.demo2-csp.athens.intrasoft-intl.private
## We're self signing our own server cert here.  This is a no-no in production.
# openssl x509 -req -days 365 -in csp-external.csr -CA ../ca/common-external-ca.crt -CAkey ../ca/common-external-ca.key -set_serial 02 -out csp-external.crt
# openssl pkcs12 -export -clcerts -in csp-external.crt -inkey csp-external.key -out csp-external.p12
# keytool -importkeystore -srckeystore csp-external.p12 -srcstoretype pkcs12 -destkeystore csp-external.jks -deststoretype JKS
#
## Verify Server Certificate
# openssl verify -purpose sslserver -CAfile ./ca/common-external-ca.crt ./server/csp-external.crt
#
## Verify Client Certificate
# openssl verify -purpose sslclient -CAfile ./ca/common-external-ca.crt ./server/csp-external.crt

## Convert Client Key to PKCS
# openssl pkcs12 -export -clcerts -in csp-external.crt -inkey csp-external.key -out csp-external.p12

## ON LINUX
# curl -v -s -k --key ./csp-external.key --cert ./csp-external.crt https://es.demo2-csp.intrasoft-intl.com https://localhost

## ON OSX
# curl -v -k -E csp-external.p12:123456 https://localhost:8443


#### Internal
## Create the Server Key, CSR, and Certificate
#
# openssl genrsa -out csp-internal.key 1024 -- No passphrase
# openssl req -new -key csp-internal.key -out csp-internal.csr
#*.local.demo2-csp.athens.intrasoft-intl.private
## We're self signing our own server cert here.  This is a no-no in production.
# openssl x509 -req -days 365 -in csp-internal.csr -CA ../ca/common-internal-ca.crt -CAkey ../ca/common-internal-ca.key -set_serial 02 -out csp-internal.crt
# openssl pkcs12 -export -clcerts -in csp-internal.crt -inkey csp-internal.key -out csp-internal.p12
# keytool -importkeystore -srckeystore csp-internal.p12 -srcstoretype pkcs12 -destkeystore csp-internal.jks -deststoretype JKS
#
## Verify Server Certificate
# openssl verify -purpose sslserver -CAfile ./ca/common-internal-ca.crt ./server/csp-internal.crt
#
## Verify Client Certificate
# openssl verify -purpose sslclient -CAfile ./ca/common-internal-ca.crt ./server/csp-internal.crt

## Convert Client Key to PKCS
# openssl pkcs12 -export -clcerts -in csp-internal.crt -inkey csp-internal.key -out csp-internal.p12

## ON LINUX
# curl -v -s -k --key ./csp-internal.key --cert ./csp-internal.crt https://es.local.demo2-csp.intrasoft-intl.com https://localhost

## ON OSX
# curl -v -k -E csp-internal.p12:123456 https://localhost:8443