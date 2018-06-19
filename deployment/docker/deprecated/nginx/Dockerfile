FROM csp-alpine35glibc:1.0

MAINTAINER Orestis Akrivopoulos

# Install nginx
RUN echo "http://dl-4.alpinelinux.org/alpine/v3.5/main" >> /etc/apk/repositories && \
    apk add --update nginx=1.10.3-r0 && \
    rm -rf /var/cache/apk/* && \
    chown -R nginx:www-data /var/lib/nginx && \
    sed -i '$iinclude /etc/nginx/csp-sites/*.conf;' /etc/nginx/nginx.conf && \
    echo "daemon off;" >> /etc/nginx/nginx.conf && \
    mkdir -p /run/nginx

EXPOSE 443
CMD ["nginx"]