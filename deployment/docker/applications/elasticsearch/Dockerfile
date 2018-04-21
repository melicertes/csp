FROM docker.elastic.co/elasticsearch/elasticsearch:5.4.0

MAINTAINER Orestis Akrivopoulos

USER root
RUN mkdir /tmp/data && chown -R elasticsearch:elasticsearch /tmp/data
COPY csp-index.tar.gz /backup/csp-index.tar.gz
RUN tar xvf /backup/csp-index.tar.gz -C /backup/ && rm /backup/csp-index.tar.gz && chown -R elasticsearch:elasticsearch /backup

USER elasticsearch

ADD elasticsearch.yml config/elasticsearch.yml
ADD wait-for-it.sh /utils/wait-for-it.sh
ADD init_index.sh /utils/init_index.sh
ADD init_csp_index.sh /utils/init_csp_index.sh
ADD restore.sh /utils/restore.sh
ADD docker-entrypoint.sh /usr/local/bin/
ADD es-docker-init /usr/share/elasticsearch/bin/es-docker-init
USER root
RUN ln -s usr/local/bin/docker-entrypoint.sh / # backwards compat
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /utils/init_index.sh
RUN chmod +x /utils/init_csp_index.sh
RUN chmod +x /usr/share/elasticsearch/bin/es-docker-init
RUN mkdir -p /opt/config
RUN chown -R elasticsearch:elasticsearch /utils
RUN chown elasticsearch:elasticsearch /docker-entrypoint.sh
RUN chown -R elasticsearch:elasticsearch /opt/config
RUN chown elasticsearch:elasticsearch /usr/share/elasticsearch/bin/es-docker-init
USER elasticsearch

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/bin/bash", "bin/es-docker"]