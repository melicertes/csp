FROM docker:18.05

MAINTAINER Orestis Akrivopoulos

ENV LANG=C.UTF-8

RUN mkdir -p /opt/csplogs/

RUN apk add --no-cache --virtual=build-dependencies wget ca-certificates unzip && update-ca-certificates && update-ca-certificates &&  apk add openjdk8 \
   && apk del build-dependencies && java -version

RUN apk add --no-cache bash drill wget ca-certificates libressl python py2-pip py-jinja2 git curl py-pip python-dev libffi-dev openssl-dev gcc  libc-dev  make \
   && pip install --upgrade pip \
   && pip install j2cli[yaml] && update-ca-certificates
RUN pip install docker-compose


COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh / # backwards compat
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]
