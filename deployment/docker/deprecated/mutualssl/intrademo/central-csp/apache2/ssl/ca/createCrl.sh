#!/usr/bin/env bash

openssl ca -config ca.conf -gencrl -keyfile common-external-ca.key -cert common-external-ca.crt -out root-external.crl.pem
