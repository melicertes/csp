FROM docker:18.02

RUN apk add --no-cache bash \
                     curl \
                     libressl

COPY run.sh /

ENTRYPOINT ["/run.sh"]