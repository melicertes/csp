FROM csp-alpine35glibc:1.0

MAINTAINER tku

# Install redis
RUN apk add --update vim less redis && \
    rm -rf /var/cache/apk/* && \
    mkdir /data && \
    chown -R redis:redis /data && \
    echo -e "# placeholder for local options\n" > /etc/redis-local.conf && \
    echo -e "logfile /var/log/redis/CSP.REDIS-exc.log" >> /etc/redis-local.conf && \
    echo -e "protected-mode no" >> /etc/redis-local.conf && \
    echo -e "bind csp-redis localhost" >> /etc/redis-local.conf && \
    echo -e "daemonize no" >> /etc/redis-local.conf && \
    echo -e "dir /data" >> /etc/redis-local.conf && \
    echo -e "loglevel notice" >> /etc/redis-local.conf && \
    echo -e "include /etc/redis-local.conf\n" >> /etc/redis.conf && \
    echo -e "vm.overcommit_memory=1" >> /etc/sysctl.conf && \
    mkdir /scripts 

ADD scripts/run.sh /scripts/run.sh

RUN chown -R redis:redis /scripts && \
    chmod u+x /scripts/run.sh

USER redis
# Expose the ports for redis
EXPOSE 6379

ENTRYPOINT ["/scripts/run.sh"]

