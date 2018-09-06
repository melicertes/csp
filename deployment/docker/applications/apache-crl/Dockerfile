FROM docker:18.02

RUN apk add --no-cache bash \
                     curl \
                     libressl

COPY run.sh /
COPY ca.conf /internalCerts

ENTRYPOINT ["/run.sh"]