FROM csp-alpine35glibc:1.0

MAINTAINER tku

# version of RT
ENV RT_VERSION=4.4.2
# version of RT::IR extension
ENV RT_IR_VERSION=4.0.0
# address of the RT host
ENV RT_HOSTNAME=csp-rt
# address of the postgresql host
ENV RT_DB_HOST=csp-postgres
# port of the postgresql
ENV RT_DB_PORT=5432
# type of the db to be used by RT -> here postgresql
ENV RT_DB_TYPE=Pg

RUN set -x && \
  apk update && \
  apk upgrade && \
  apk --update add lighttpd perl wget vim fcgi postfix-pcre openssl \
    postgresql-client perl-lwp-protocol-https perl-dbd-pg perl-dbd-mysql \
    perl-dbd-sqlite perl-cgi-psgi perl-cgi perl-fcgi perl-term-readkey \
    perl-xml-rss perl-crypt-ssleay perl-crypt-eksblowfish perl-crypt-x509 \
    perl-html-mason-psgihandler perl-fcgi-procmanager perl-mime-types \
    perl-list-moreutils perl-json perl-html-quoted perl-html-scrubber \
    perl-email-address perl-text-password-pronounceable perl-email-address-list \
    perl-html-formattext-withlinks-andtables perl-html-rewriteattributes \
    perl-text-wikiformat perl-text-quoted perl-datetime-format-natural \
    perl-date-extract perl-data-guid perl-data-ical perl-string-shellquote \
    perl-convert-color perl-dbix-searchbuilder perl-file-which perl-css-squish \
    perl-tree-simple perl-plack perl-log-dispatch perl-module-versions-report \
    perl-symbol-global-name perl-devel-globaldestruction perl-parallel-prefork \
    perl-cgi-emulate-psgi perl-text-template perl-net-cidr perl-apache-session \
    perl-locale-maketext-lexicon perl-locale-maketext-fuzzy perl-graphviz \
    perl-regexp-common-net-cidr perl-module-refresh perl-date-manip \
    perl-regexp-ipv6 perl-text-wrapper perl-universal-require perl-role-basic \
    perl-convert-binhex perl-test-sharedfork perl-test-tcp perl-server-starter \
    perl-starlet make gnupg gcc perl-dev libc-dev less expat-dev perl-gd gd lua && \
  rm -f /var/cache/apk/* && \
  wget -O /tmp/rt-$RT_VERSION.tar.gz https://download.bestpractical.com/pub/rt/release/rt-$RT_VERSION.tar.gz && \
  wget -O /tmp/RT-IR-$RT_IR_VERSION.tar.gz https://download.bestpractical.com/pub/rt/release/RT-IR-$RT_IR_VERSION.tar.gz && \
  tar -xvz -C /tmp -f /tmp/rt-$RT_VERSION.tar.gz && \
  cd /tmp/rt-$RT_VERSION && \
  (echo y;echo o conf prerequisites_policy follow;echo o conf commit)|cpan && \
  cpan -f GnuPG::Interface && \
  cpan -f Capture::Tiny && \
  cpan -f Time::ParseDate && \
  cpan -f REST::Client && \
  ./configure --with-web-user=lighttpd --with-web-group=lighttpd --enable-gpg --enable-gd \
    --enable-graphviz --with-db-type=${RT_DB_TYPE} --with-db-host=${RT_DB_HOST} \
    --with-db-port=${RT_DB_PORT} --with-db-rt-host=${RT_HOSTNAME} \
    --enable-externalauth && \
  make fixdeps && \
  make install && \
  mkdir /var/run/lighttpd/ && \
  touch /var/run/lighttpd/lighttpd-fcgi.sock-0 && \
  chown -R lighttpd:lighttpd /var/run/lighttpd && \
  mkdir /scripts && \
  mkdir /scripts/rt && \
  mkdir -p /opt/rt4/local/lib/RT/Action && \
  mkdir -p /opt/rt4/local/lib/RT/Condition && \
  mkdir -p /opt/rt4/local/lib/RT/CustomFieldValues && \
  mv /opt/rt4/etc/initialdata /opt/rt4/etc/initialdata.orig && \
  mkdir -p /opt/rt4/var/log && \
  touch /opt/rt4/var/log/CSP.RT-exc.log && \
  chown -R lighttpd:lighttpd /opt/rt4/var

ADD config/mod_fastcgi.conf /etc/lighttpd/
ADD config/lighttpd.conf /etc/lighttpd/
ADD config/remote_user.lua /etc/lighttpd/
ADD config/RT_SiteConfig.pm /opt/rt4/etc/
ADD scripts/run.sh /scripts/run.sh
ADD scripts/reinitdb.sh /scripts/reinitdb.sh
ADD scripts/dropdb.sh /scripts/dropdb.sh
ADD scripts/initdb.sh /scripts/initdb.sh
ADD scripts/initrt.sh /scripts/initrt.sh
ADD scripts/cleandb.sh /scripts/cleandb.sh
ADD scripts/uninit-rt.sh /scripts/uninit-rt.sh
ADD config/custom-actions/SetUUIDAction.pm /opt/rt4/local/lib/RT/Action/SetUUIDAction.pm
ADD config/custom-actions/CSPOnCreateAction.pm /opt/rt4/local/lib/RT/Action/CSPOnCreateAction.pm
ADD config/custom-conditions/CSP_ToEmitter.pm /opt/rt4/local/lib/RT/Condition/CSP_ToEmitter.pm
ADD config/custom-field-values/CustomTCSource.pm /opt/rt4/local/lib/RT/CustomFieldValues/CustomTCSource.pm
# only temp solution, should be replaced by directly call to TC
ADD config/custom-field-values/tc-list.json /opt/rt4/local/lib/RT/CustomFieldValues/tc-list.json
ADD config/custom-field-values/teams-list.json /opt/rt4/local/lib/RT/CustomFieldValues/teams-list.json
# end of temp solution
ADD config/initialdata.csp /opt/rt4/etc/initialdata
ADD config/additional-initialdata.csp /opt/rt4/etc/additional-initialdata.csp

RUN chown lighttpd:lighttpd /opt/rt4/etc/RT_SiteConfig.pm && \
  chown lighttpd:lighttpd /etc/lighttpd/* && \
  chmod 400 /etc/lighttpd/* && \
  cd /tmp && \
  tar -xzf RT-IR-$RT_IR_VERSION.tar.gz && \
  cd RT-IR-${RT_IR_VERSION} && \
  cpan Parse::BooleanLogic && \
  cpan Cpanel::JSON::XS && \
  cpan JSON::XS && \
  perl Makefile.PL && \
  make && \
  make install && \
  cd / && \
  chmod -R 0755 /scripts && \
  rm -f /tmp/rt-$RT_VERSION.tar.gz && \
  rm -f /tmp/RT-IR-$RT_IR_VERSION.tar.gz && \
  echo "DONE SUCCESSFULLY !!!!"

EXPOSE 80

ENTRYPOINT ["/scripts/run.sh"]

