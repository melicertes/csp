#!/bin/bash


CRL_DATE_FORMAT=${CRL_DATE_FORMAT:-'%Y-%m-%dT%H:%M:%SZ'}
CRL_URL=${CRL_URL:-'https://www.sk-cert.sk/pki/melicertes-ca/pub/crl/cacrl.crl'}
CRL_INTERVAL=${CRL_INTERVAL:-'600'}

log() {
  echo "$(date -u +${CRL_DATE_FORMAT}) ${@}"
}

mkdir -p crl
touch /tmp/cacrl.md5sum

touch /internalCerts/certindex
echo 01 > /internalCerts/certserial
echo 01 > /internalCerts/crlnumber

cd /internalCerts/
cp /ca.conf .
openssl ca -config ca.conf -gencrl -keyfile internalCA.key -cert internalCA.crt -out root.crl.pem
cp /internalCerts/root.crl.pem /etc/apache2/ssl/crl/

# Infinite loop
while true; do

    log "Downloading CRL from ${CRL_URL}"
    wget -O /tmp/cacrl.pem ${CRL_URL}
    md5new=$(md5sum /tmp/cacrl.pem)
    md5Old=$(cat /tmp/cacrl.md5sum)

    if [ "$md5new" == "$md5Old" ]; then
        log "CRL list is the same."
    else
        log "New CRL list received."
        rm /etc/apache2/ssl/crl/*
        md5sum /tmp/cacrl.pem > /tmp/cacrl.md5sum
        cp /tmp/cacrl.pem /etc/apache2/ssl/crl/
        cp /internalCerts/root.crl.pem /etc/apache2/ssl/crl/
        c_rehash /etc/apache2/ssl/crl/

        log "Restarting Apache"
        docker restart csp-apache
    fi

    log "Sleeping for ${CRL_INTERVAL}"
    sleep "${CRL_INTERVAL}"
done
