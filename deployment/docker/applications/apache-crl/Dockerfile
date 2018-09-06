FROM docker:18.02

RUN apk add --no-cache bash \
                     curl \
                     libressl

COPY run.sh /
RUN mkdir -p /internalCerts
COPY ca.conf /

ENTRYPOINT ["/run.sh"]