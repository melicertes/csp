FROM ubuntu:16.04


MAINTAINER tku
MAINTAINER msg


#RUN apt-get clean &&  apt-get update &&  apt-get -y upgrade && \
RUN apt-get update &&   \
 apt-get install -y python3-pip python3-dnspython python3-psutil python3-redis python3-requests python3-termstyle python3-tz python3-bs4 && \
 apt-get update -y && \
 apt-get install -y apt-utils git apache2 php libapache2-mod-php7.0 && \
 apt-get install -y sudo build-essential libcurl4-gnutls-dev libgnutls28-dev python3-dev && \
# apt-get install -y nano vim && \
 rm -rf /var/lib/apt/lists/*checkout

RUN git clone https://github.com/certtools/intelmq.git /opt/dev_intelmq
WORKDIR /opt/dev_intelmq
RUN git checkout tags/1.1.1
#RUN git checkout

ADD test-data /tmp/test-data
ADD csp-config/cve /opt/dev_intelmq/intelmq/bots/parsers/cve
COPY ./csp-config/BOTS /opt/dev_intelmq/intelmq/bots/BOTS
COPY ./csp-config/BOTS /opt/intelmq/etc/BOTS

RUN pip3 install . && \
 cp /opt/intelmq/etc/examples/harmonization.conf /opt/intelmq/etc/ && \
 useradd -d /opt/intelmq -U -s /bin/bash intelmq && \
 mkdir -p /opt/intelmq/var/log && \
#review chmod -R 775 /opt/intelmq && \
 chown -R intelmq:intelmq /opt/intelmq

RUN git clone https://github.com/certtools/intelmq-manager.git /tmp/intelmq-manager
WORKDIR /tmp/intelmq-manager
RUN git checkout tags/1.1.0

RUN cp -R /tmp/intelmq-manager/intelmq-manager/* /var/www/html/ && \
 mv /var/www/html/index.html /var/www/html/index.html.save && \
 chown -R www-data:www-data /var/www/html/ && \
 usermod -a -G intelmq www-data && \
 mkdir /opt/intelmq/etc/manager/ 

ADD positions.conf /opt/intelmq/etc/manager/positions.conf

RUN chmod 666  /opt/intelmq/etc/manager/positions.conf && \
 rm -rf /tmp/intelmq-manager

COPY ./csp-config/*.conf /opt/intelmq/etc/
RUN chown intelmq:www-data /opt/intelmq/etc/*.conf && \
 chmod g+w /opt/intelmq/etc/*.conf && \
 echo "www-data ALL=(intelmq) NOPASSWD: /usr/local/bin/intelmqctl" >> /etc/sudoers && \
 echo "intelmq ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

ENV INTELMQ_HOSTNAME=csp-intelmq
RUN sed -i "s/#ServerName www.example.com/ServerName ${INTELMQ_HOSTNAME}/g" /etc/apache2/sites-enabled/000-default.conf && \
 sed -i "s/#ServerName www.example.com/ServerName ${INTELMQ_HOSTNAME}/g" /etc/apache2/sites-available/000-default.conf 


ENV REDIS_HOSTNAME=csp-redis
RUN cd /opt/intelmq/etc && \
  sed -i "s/\"destination_pipeline_host\": \"127.0.0.1\"/\"destination_pipeline_host\": \"${REDIS_HOSTNAME}\"/g" defaults.conf && \
  sed -i "s/\"source_pipeline_host\": \"127.0.0.1\"/\"source_pipeline_host\": \"${REDIS_HOSTNAME}\"/g" defaults.conf 
  
RUN  mkdir -p /tmp/intelmq-fileinput && \
  chmod og+rw -R /tmp/intelmq-fileinput && \
  mkdir -p /tmp/intelmq-fileoutput && \
  chmod og+rw -R /tmp/intelmq-fileoutput && \
  mkdir /scripts && \
  echo "Added CSP custom fileinput-output"

ADD scripts/run.sh /scripts/run.sh
RUN chmod u+x /scripts/run.sh && \
 chmod -R 0770 /opt/intelmq/etc/ && \
    cd /opt/intelmq/etc && \
    sed -i "s/\"redis_cache_host\": \"127.0.0.1\",/\"redis_cache_host\": \"${REDIS_HOSTNAME}\",/g" runtime.conf && \
    chown -R intelmq.intelmq /opt/intelmq/etc/ && \
    echo "Added CSP custom runtime done" && \
    echo "DONE SUCCESSFULLY!"

ENTRYPOINT ["/scripts/run.sh"]

