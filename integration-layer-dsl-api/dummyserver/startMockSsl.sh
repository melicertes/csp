#!/usr/bin/env bash

APP_NAME=adapter SSL=true SSL_CA=/data/common/sslcert/ca.crt SSL_CERT=/data/common/sslcert/csp-internal.crt SSL_KEY=/data/common/sslcert/csp-internal.key PORT=8082 node server.js > mock.out
