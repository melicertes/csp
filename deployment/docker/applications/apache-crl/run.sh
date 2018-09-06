#!/bin/bash


CRL_DATE_FORMAT=${CRL_DATE_FORMAT:-'%Y-%m-%dT%H:%M:%SZ'}
CRL_URL=${CRL_URL:-'https://pki.dfn-cert.de/melicertes-ca/pub/crl/cacrl.crl'}
CRL_INTERVAL=${CRL_INTERVAL:-'600'}

log() {
  echo "$(date -u +${CRL_DATE_FORMAT}) ${@}"
}

mkdir -p crl
touch /tmp/cacrl.md5sum

touch /internalCerts/certindex
echo 01 > /internalCerts/certserial
echo 01 > /internalCerts/crlnumber

cd /internalCert/
openssl ca -config ca.conf -gencrl -keyfile internalCA.key -cert internalCA.crt -out root.crl.pem
cp /internalCert/root.crl.pem /etc/apache2/ssl/crl/

# Infinite loop
while true; do

    log "Downloading CRL from ${CRL_URL}"
    wget -O /tmp/cacrl.crl ${CRL_URL}
    md5new=$(md5sum /tmp/cacrl.crl)
    md5Old=$(cat /tmp/cacrl.md5sum)

    if [ "$md5new" == "$md5Old" ]; then
        log "CRL list is the same."
    else
        log "New CRL list received."
        rm /etc/apache2/ssl/crl/*
        md5sum /tmp/cacrl.crl > /tmp/cacrl.md5sum
        openssl crl -inform DER -in /tmp/cacrl.crl -outform PEM -out /tmp/cacrl.pem
        cp /tmp/cacrl.pem /etc/apache2/ssl/crl/
        cp /internalCert/root.crl.pem /etc/apache2/ssl/crl/
        c_rehash /etc/apache2/ssl/crl/

        log "Restarting Apache"
        docker restart csp-apache
    fi

    log "Sleeping for ${CRL_INTERVAL}"
    sleep "${CRL_INTERVAL}"
done