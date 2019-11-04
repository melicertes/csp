FROM openjdk:8-alpine
# simple base to be used as the base of all our docker files in CSP
MAINTAINER Thanos Angelatos

ENV LANG=C.UTF-8
RUN apk --no-cache add tzdata && cp /usr/share/zoneinfo/UTC /etc/localtime && echo "UTC" > /etc/timezone && mkdir -p /opt/csplogs/ && \
  echo "https://uk.alpinelinux.org/alpine/v3.9/main" > /etc/apk/repositories && \
  echo "https://uk.alpinelinux.org/alpine/v3.9/community" >> /etc/apk/repositories
