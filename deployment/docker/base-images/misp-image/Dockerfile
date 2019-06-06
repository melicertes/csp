FROM csp-alpine35glibc:1.0 as builder_2.4.107

RUN apk add --no-cache git
WORKDIR /var/www
#RUN chown www-data:www-data /var/www
#USER www-data
RUN git clone https://github.com/MISP/MISP.git
WORKDIR /var/www/MISP
#RUN git checkout tags/$(git describe --tags `git rev-list --tags --max-count=1000`)
RUN git checkout tags/$(git describe --tags v2.4.107)
RUN git config core.filemode false

WORKDIR /var/www/MISP/app/files/scripts
RUN git clone https://github.com/CybOXProject/python-cybox.git
RUN git clone https://github.com/STIXProject/python-stix.git

WORKDIR /var/www/MISP/app/files/scripts/python-cybox
RUN git checkout v2.1.0.12

#USER www-data
WORKDIR /var/www/MISP/app/files/scripts/python-stix
RUN git checkout v1.1.1.4

#USER www-data
WORKDIR /var/www/MISP
RUN git submodule init
RUN git submodule update

# Install MISP Modules
WORKDIR /opt
RUN git clone https://github.com/MISP/misp-modules.git


FROM ubuntu:16.04
MAINTAINER Christos Panagiotou

# Install core components
ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get dist-upgrade -y && apt-get autoremove -y && apt-get clean
RUN apt-get install -y software-properties-common
RUN apt-get install -y postfix
RUN apt-get install -y mysql-client curl gcc gnupg-agent make python openssl redis-server sudo vim zip locales

RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
RUN add-apt-repository -y ppa:ondrej/php && apt-get update

# Apache
RUN apt-get install -y apache2 apache2-doc apache2-utils
RUN a2dismod status
RUN a2dissite 000-default

# PHP 7.2
RUN apt-get install -y libapache2-mod-php php7.2 php7.2-cli php-crypt-gpg php7.2-dev php7.2-json php7.2-mysql php7.2-opcache php7.2-readline php7.2-redis php7.2-xml git

# Fix php.ini with recommended settings
RUN sed -i "s/max_execution_time = 30/max_execution_time = 300/" /etc/php/7.2/apache2/php.ini
RUN sed -i "s/memory_limit = 128M/memory_limit = 512M/" /etc/php/7.2/apache2/php.ini
RUN sed -i "s/upload_max_filesize = 2M/upload_max_filesize = 50M/" /etc/php/7.2/apache2/php.ini
RUN sed -i "s/post_max_size = 8M/post_max_size = 50M/" /etc/php/7.2/apache2/php.ini

RUN apt-get install -y python-dev python-pip libxml2-dev libxslt1-dev zlib1g-dev python-setuptools
RUN apt-get install -y cron logrotate supervisor syslog-ng-core
RUN apt-get clean

WORKDIR /var/www

USER www-data
COPY --from=builder_2.4.107 /var/www/ .

WORKDIR /var/www/MISP/app/files/scripts/python-cybox
USER root
RUN python setup.py install

USER www-data
WORKDIR /var/www/MISP/app/files/scripts/python-stix
USER root
RUN python setup.py install
RUN chown -R www-data:www-data /var/www

USER www-data
WORKDIR /var/www/MISP/app
RUN php composer.phar config vendor-dir Vendor
RUN php composer.phar install
USER root
RUN phpenmod redis
USER www-data
RUN cp -fa /var/www/MISP/INSTALL/setup/config.php /var/www/MISP/app/Plugin/CakeResque/Config/config.php

# Fix permissions
USER root
RUN chown -R www-data:www-data /var/www/MISP
RUN chmod -R 750 /var/www/MISP
RUN chmod -R g+ws /var/www/MISP/app/tmp
RUN chmod -R g+ws /var/www/MISP/app/files
RUN chmod -R g+ws /var/www/MISP/app/files/scripts/tmp

RUN cp /var/www/MISP/INSTALL/misp.logrotate /etc/logrotate.d/misp

# Preconfigure setting for packages
RUN echo "postfix postfix/main_mailer_type string Local only" | debconf-set-selections
RUN echo "postfix postfix/mailname string localhost.localdomain" | debconf-set-selections

# Redis Setup
RUN sed -i 's/^\(daemonize\s*\)yes\s*$/\1no/g' /etc/redis/redis.conf

# Install PEAR packages
RUN pear install Crypt_GPG >>/tmp/install.log
RUN pear install Net_GeoIP

# Apache Setup
RUN cp /var/www/MISP/INSTALL/apache.misp.ubuntu /etc/apache2/sites-available/misp.conf
RUN sed -i -E 's/80/800/' /etc/apache2/sites-available/misp.conf
RUN sed -i -E 's/80/800/' /etc/apache2/ports.conf
RUN a2dissite 000-default
RUN a2ensite misp
RUN a2enmod rewrite
RUN a2enmod headers

# MISP base configuration
RUN sudo -u www-data cp -a /var/www/MISP/app/Config/bootstrap.default.php /var/www/MISP/app/Config/bootstrap.php
RUN sudo -u www-data cp -a /var/www/MISP/app/Config/database.default.php /var/www/MISP/app/Config/database.php
RUN sudo -u www-data cp -a /var/www/MISP/app/Config/core.default.php /var/www/MISP/app/Config/core.php
RUN sudo -u www-data cp -a /var/www/MISP/app/Config/config.default.php /var/www/MISP/app/Config/config.php
RUN chown -R www-data:www-data /var/www/MISP/app/Config
RUN chmod -R 750 /var/www/MISP/app/Config

# Replace the default salt
RUN sed -i -E "s/'salt'\s=>\s'(\S+)'/'salt' => '`openssl rand -base64 32|tr "/" "-"`'/" /var/www/MISP/app/Config/config.php

# Enable workers at boot time
RUN chmod a+x /var/www/MISP/app/Console/worker/start.sh
RUN echo "sudo -u www-data bash /var/www/MISP/app/Console/worker/start.sh" >>/etc/rc.local

# Install MISP Modules
WORKDIR /opt
RUN apt-get update
RUN apt-get install -y python3 python3-pip libjpeg-dev
COPY --from=builder_2.4.107 /opt/ .
ADD misp-modules /opt/misp-modules
WORKDIR /opt/misp-modules
RUN pip3 install --upgrade pip
RUN cat REQUIREMENTS | sed 's/aiohttp==3.4.4/aiohttp/g' > REQUIREMENTS
RUN pip3 install --upgrade --ignore-installed urllib3
RUN pip3 install --upgrade --ignore-installed requests
RUN sed -i 's/aiohttp.*/aiohttp/g' REQUIREMENTS
RUN sed -i 's/functools.*//g' REQUIREMENTS
RUN sed -i 's/async-timeout.*/async-timeout/g' REQUIREMENTS
RUN sed -i 's/url-normalize.*/url-normalize/g' REQUIREMENTS
RUN sed -i 's/^\(yarl\)\=.*/\1/g' REQUIREMENTS
RUN sed -i 's/^\(sigmatools\)\=.*/\1/' REQUIREMENTS
RUN pip3 install -I -r REQUIREMENTS
RUN pip3 install -I .
RUN pip install zmq redis
RUN pip3 install zmq redis
RUN echo "sudo -u www-data misp-modules -s &" >>/etc/rc.local

# Supervisord Setup
RUN echo '[supervisord]' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'nodaemon = true' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '[program:postfix]' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'process_name = master' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'directory = /etc/postfix' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'command = /usr/sbin/postfix -c /etc/postfix start' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'startsecs = 0' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'autorestart = false' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '[program:redis-server]' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'command=redis-server /etc/redis/redis.conf' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '[program:apache2]' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'command=/bin/bash -c "source /etc/apache2/envvars && exec /usr/sbin/apache2 -D FOREGROUND"' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '[program:resque]' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'command=/bin/bash /var/www/MISP/app/Console/worker/start.sh' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'user = www-data' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'startsecs = 0' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'autorestart = false' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo '[program:misp-modules]' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'command=/bin/bash -c "cd /opt/misp-modules/bin && /usr/bin/python3 misp-modules.py"' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'user = root' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'startsecs = 0' >> /etc/supervisor/conf.d/supervisord.conf
RUN echo 'autorestart = false' >> /etc/supervisor/conf.d/supervisord.conf

# Modify syslog configuration
RUN sed -i -E 's/^(\s*)system\(\);/\1unix-stream("\/dev\/log");/' /etc/syslog-ng/syslog-ng.conf

# Add run script
ADD run.sh /run.sh
ADD csp_configuration /csp_configuration
RUN chmod 0755 /run.sh

# Trigger to perform first boot operations
RUN touch /.firstboot.tmp

RUN touch /var/www/MISP/app/tmp/logs/error.log
RUN touch /var/www/MISP/app/tmp/logs/debug.log
RUN chmod -R 777 /var/www/MISP/app/tmp/logs/
RUN chown -R www-data:www-data /var/www/MISP/app/tmp/logs/
VOLUME ["/var/www/MISP/app/tmp/logs"]

# Make a backup of /var/www/MISP to restore it to the local moint point at first boot
WORKDIR /var/www/MISP

# Add misp-objects for CSP support
COPY csp-misp-objects/ app/files/misp-objects/objects/
RUN chown -R www-data:www-data /var/www/MISP/app/files/

# Final archive
RUN tar czpf /root/MISP.tgz .

#COPY AppController.php /var/www/MISP/app/Controller/AppController.php
#VOLUME /var/www/MISP
EXPOSE 80
ENTRYPOINT ["/run.sh"]
