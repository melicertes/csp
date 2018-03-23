echo "Dockerized nginx with mutual ssl authenticationg for testing"
unzip csp.zip -d /opt
chown -R nobody: /opt/csp/

## Create the CA Key and Certificate for signing Client Certs
# openssl genrsa -des3 -out ca.key 4096
# openssl req -new -x509 -days 365 -key ca.key -out ca.crt
#
## Create the Server Key, CSR, and Certificate
# openssl genrsa -des3 -out server.key 1024
# openssl genrsa -out server.key 1024 -- No passphrase
# openssl req -new -key server.key -out server.csr
#
## We're self signing our own server cert here.  This is a no-no in production.
# openssl x509 -req -days 365 -in server.csr -CA ../ca/ca.crt -CAkey ../ca/ca.key -set_serial 01 -out server.crt
#
## Create the Client Key and CSR
# openssl genrsa -des3 -out client.key 1024
# openssl genrsa  -out client.key 1024 -- No passphrase
# openssl req -new -key client.key -out client.csr
#
## Sign the client certificate with our CA cert.  Unlike signing our own server cert, this is what we want to do.
## Serial should be different from the server one, otherwise curl will return NSS error -8054
# openssl x509 -req -days 365 -in client.csr -CA ../ca/ca.crt -CAkey ../ca/ca.key -set_serial 02 -out client.crt
#
## Verify Server Certificate
# openssl verify -purpose sslserver -CAfile ./ca/ca.crt ./server/server.crt
#
## Verify Client Certificate
# openssl verify -purpose sslclient -CAfile ./ca/ca.crt ./client/client.crt

## Convert Client Key to PKCS
# openssl pkcs12 -export -clcerts -in client.crt -inkey client.key -out client.p12

## ON LINUX
# curl -v -s -k --key client.key --cert client.crt https://localhost

## ON OSX
# curl -v -k -E client.p12:1234 https://localhost:8443