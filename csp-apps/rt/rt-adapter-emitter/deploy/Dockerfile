FROM csp-java8:1.0

MAINTAINER tku

ADD startit.sh /opt/csp

RUN apk update && \
  apk add curl wget vim less && \
  chmod 0777 /opt/csp 


EXPOSE 8081

ENTRYPOINT ["/opt/csp/startir.sh"]
