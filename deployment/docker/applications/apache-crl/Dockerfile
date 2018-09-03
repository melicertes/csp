FROM csp-docker-java8:1.0

RUN apk add --no-cache bash \
                     curl \
                     openssl

COPY run.sh /

ENTRYPOINT ["/run.sh"]