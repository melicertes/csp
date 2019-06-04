FROM debian:stretch

RUN apt-get -y update \
 && apt-get -y install --no-install-recommends gnupg2 ca-certificates wget procps \
      dnsutils nginx bash pwgen net-tools

RUN wget -qO - https://download.jitsi.org/jitsi-key.gpg.key | apt-key add - \
 && echo 'deb http://download.jitsi.org stable/' >> /etc/apt/sources.list \
 && apt-get -y update \
 && apt-get -y install jitsi-meet jitsi-videobridge jicofo jitsi-meet-web jitsi-meet-web-config jitsi-meet-prosody

RUN rm -rf /etc/nginx/sites-enabled/* \
 && rm -rf /etc/prosody/conf.d/*

COPY config/jitsi /etc/jitsi
RUN chown -R jicofo: /etc/jitsi/jicofo \
 && chown -R jvb: /etc/jitsi/videobridge

COPY config/nginx.conf /etc/nginx/nginx.conf
COPY config/prosody.cfg.lua /etc/prosody/prosody.cfg.lua

COPY start.sh /start.sh

# making sure this will start
RUN chmod 770 /start.sh

ENV DOMAIN=test.com STUN=stun.ekiga.net BRIDGE_IP=1.2.3.4 BRIDGE_TCP_PORT=4443 BRIDGE_UDP_PORT=10000

EXPOSE 80 4443 10000/udp

CMD /start.sh

