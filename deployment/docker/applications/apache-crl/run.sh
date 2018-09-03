#!/bin/bash


CRL_DATE_FORMAT=${CRL_DATE_FORMAT:-'%Y-%m-%dT%H:%M:%SZ'}
CRL_URL=${CRL_URL:-'https://pki.dfn-cert.de/melicertes-ca/pub/crl/cacrl.crl'}
CRL_INTERVAL=${CRL_INTERVAL:-'600'}

log() {
  echo "$(date -u +${CRL_DATE_FORMAT}) ${@}"
}

mkdir -p crl
touch /tmp/cacrl.md5sum

# Infinite loop
while true; do

    log "Downloading CRL from ${CRL_URL}"
    wget -O ${CRL_URL} /tmp/cacrl.crl
    md5new=$(md5sum /tmp/cacrl.crl)
    md5Old=$(cat /tmp/cacrl.md5sum)

    if [ "$md5new" == "$md5Old" ]; then
        log "CRL list is the same."
    else
        log "New CRL list received."
        md5sum /tmp/cacrl.crl > /tmp/cacrl.md5sum
        cp /tmp/cacrl.crl /etc/apache2/ssl/crl
        log "Restarting Apache"
        docker restart csp-apache
    fi

    log "Sleeping for ${CRL_INTERVAL}"
    sleep "${CRL_INTERVAL}"
done