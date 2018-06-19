#FROM csp-java8:1.0
FROM httpd:2.4

MAINTAINER Orestis Akrivopoulos
MAINTAINER Thanos Angelatos

#RUN set -x \
#    && apk update && apk upgrade \
#    && apk add apache2 apache2-proxy openssl apache2-ssl libstdc++ nspr build-base nss libxml2
RUN apt-get update && apt-get install -y vim

RUN set -x \
    && rm -rf /etc/apache2 \
    && mkdir /run/apache2 /var/log/apache2 \
    && touch /var/log/apache2/access.log /var/log/apache2/error.log \
    && ln -sf /proc/self/fd/1 /var/log/apache2/access.log \
    && ln -sf /proc/self/fd/1 /var/log/apache2/error.log \
    && mkdir -p /var/www

ADD web_agents /web_agents

ADD apache2 /etc/apache2
RUN mkdir -p /etc/apache2/csp-sites
#ADD agent.conf /opt/agent.conf
ADD create-agent.sh /usr/local/bin/
RUN ln -s /usr/local/bin/create-agent.sh / # backwards compat
ADD /docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
RUN chmod +x /create-agent.sh
ADD httpd-foreground.sh /httpd-foreground.sh
RUN chmod +x /httpd-foreground.sh

CMD ["/docker-entrypoint.sh"]

