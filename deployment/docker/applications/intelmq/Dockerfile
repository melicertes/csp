FROM csp-python3:1.0

MAINTAINER tku
MAINTAINER msg

ENV INTELMQ_VERSION=1.0.1
ENV INTELMQ_MGR_VERSION=0.3.1
ENV INTELMQ_HOSTNAME=csp-intelmq
ENV REDIS_HOSTNAME=csp-redis

RUN apk update && \
  apk upgrade && \
  apk add wget vim sudo php5-apache2 php5-curl php5-cli php5-json python3-dev \
    alpine-sdk linux-headers postgresql-dev libffi-dev shadow && \
  wget https://github.com/certtools/intelmq/archive/${INTELMQ_VERSION}.tar.gz \
    -O /tmp/intelmq-${INTELMQ_VERSION}.tar.gz && \
  wget https://github.com/certtools/intelmq-manager/archive/${INTELMQ_MGR_VERSION}.tar.gz \
    -O /tmp/intelmq-manager-${INTELMQ_MGR_VERSION}.tar.gz && \
  cd /tmp && \
  tar -xzf intelmq-${INTELMQ_VERSION}.tar.gz && \
  tar -xzf intelmq-manager-${INTELMQ_MGR_VERSION}.tar.gz && \
  rm -f intelmq-${INTELMQ_VERSION}.tar.gz && \
  rm -f intelmq-manager-${INTELMQ_MGR_VERSION}.tar.gz && \
  cd intelmq-${INTELMQ_VERSION} && \
  pip3 install . && \
  find . -name REQUIREMENTS.txt -exec pip3 install -q -r {} \; && \
  adduser -D -h /opt/intelmq -s /bin/sh intelmq && \
  mkdir -p /opt/intelmq/var/log && \
  chmod -R 0770 /opt/intelmq && \
  chown -R intelmq.intelmq /opt/intelmq && \
  cd /opt/intelmq/etc && \
  cp -a examples/* . && \
  sed -i "s/\"destination_pipeline_host\": \"127.0.0.1\"/\"destination_pipeline_host\": \"${REDIS_HOSTNAME}\"/g" defaults.conf && \
  sed -i "s/\"source_pipeline_host\": \"127.0.0.1\"/\"source_pipeline_host\": \"${REDIS_HOSTNAME}\"/g" defaults.conf && \
  sed -i "s/\"redis_cache_host\": \"127.0.0.1\",/\"redis_cache_host\": \"${REDIS_HOSTNAME}\",/g" runtime.conf && \
  cd /tmp/intelmq-manager-${INTELMQ_MGR_VERSION} && \
  sed -i "s/#ServerName www.example.com:80/ServerName ${INTELMQ_HOSTNAME}/g" /etc/apache2/httpd.conf && \
  cp -R /tmp/intelmq-manager-${INTELMQ_MGR_VERSION}/intelmq-manager/* /var/www/localhost/htdocs/ && \
  chown -R apache:apache /var/www/localhost/htdocs/ && \
  usermod -a -G intelmq apache && \
  mkdir /opt/intelmq/etc/manager/ && \
  touch /opt/intelmq/etc/manager/positions.conf && \
  chgrp apache /opt/intelmq/etc/*.conf /opt/intelmq/etc/manager/positions.conf && \
  chmod g+w /opt/intelmq/etc/*.conf /opt/intelmq/etc/manager/positions.conf && \
  echo "apache ALL=(intelmq) NOPASSWD: /usr/bin/intelmqctl" >> /etc/sudoers && \
  sed -i "s/\/usr\/local\/bin\/intelmqctl/\/usr\/bin\/intelmqctl/g" /var/www/localhost/htdocs/php/config.php && \
  echo "root ALL=(intelmq) NOPASSWD: /usr/bin/intelmqctl" >> /etc/sudoers && \
  mkdir /scripts && \
  mkdir /run/apache2 && \
  #mkdir /tmp/intelmq-fileinput \
  chmod go+rw /tmp/intelmq-fileinput \
  echo "DONE SUCCESSFULLY!"

ADD scripts/run.sh /scripts/run.sh

ADD csp-config/runtime.conf /opt/intelmq/etc/runtime.conf
ADD csp-config/pipeline.conf /opt/intelmq/etc/pipeline.conf
RUN chmod -R 0770 /opt/intelmq/etc/ && \
    chown -R intelmq.intelmq /opt/intelmq/etc/ && \
    echo "Added CSP custom runtime done"

RUN chmod u+x /scripts/run.sh

#USER intelmq

ENTRYPOINT ["scripts/run.sh"]  




