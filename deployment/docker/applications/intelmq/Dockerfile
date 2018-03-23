FROM csp-python3:1.0
#FROM python:3.4-alpine
WORKDIR /tmp/intelmq

#clone first
RUN apk --no-cache add curl git && git clone https://github.com/certtools/intelmq.git\
 /tmp/intelmq && apk del git
#in one layer, add compilation stuff, compile and remove (so layer remains small)
RUN apk --no-cache add python3-dev gcc alpine-sdk linux-headers\
  && rm CONTRIBUTING.md && pip3 install -r REQUIREMENTS\
  && pip3 install .\
  && mv /opt/intelmq/etc/examples/*.conf /opt/intelmq/etc/\
  && apk del gcc alpine-sdk linux-headers build-base && rm -rf /var/cache/apk/*

RUN adduser -D -h /opt/intelmq -s /bin/bash intelmq && \
    chmod -R 0770 /opt/intelmq && \
    chown -R intelmq.intelmq /opt/intelmq
COPY intelmq_docker /usr/local/bin/
COPY config/* /opt/intelmq/etc/

USER intelmq
WORKDIR /opt/intelmq
CMD ["sh", "/usr/local/bin/intelmq_docker"]

