FROM docker:18.02

MAINTAINER Orestis Akrivopoulos

ENV GLIBC "2.25-r0"
RUN ALPINE_GLIBC_BASE_URL="https://github.com/sgerrand/alpine-pkg-glibc/releases/download" && \
    ALPINE_GLIBC_PACKAGE_VERSION="$GLIBC" && \
    ALPINE_GLIBC_BASE_PACKAGE_FILENAME="glibc-$ALPINE_GLIBC_PACKAGE_VERSION.apk" && \
    ALPINE_GLIBC_BIN_PACKAGE_FILENAME="glibc-bin-$ALPINE_GLIBC_PACKAGE_VERSION.apk" && \
    ALPINE_GLIBC_I18N_PACKAGE_FILENAME="glibc-i18n-$ALPINE_GLIBC_PACKAGE_VERSION.apk" && \
    apk add --no-cache --virtual=.build-dependencies wget ca-certificates && \
    wget \
        "http://central.preprod.melicertes.eu/repo-loads/alpine35base/sgerrand.rsa.pub" \
        -O "/etc/apk/keys/sgerrand.rsa.pub" && \
    wget \
        "http://central.preprod.melicertes.eu/repo-loads/alpine35base/glibc-2.25-r0.apk" \
        "http://central.preprod.melicertes.eu/repo-loads/alpine35base/glibc-bin-2.25-r0.apk" \
        "http://central.preprod.melicertes.eu/repo-loads/alpine35base/glibc-i18n-2.25-r0.apk" && \
    apk add --no-cache \
        "$ALPINE_GLIBC_BASE_PACKAGE_FILENAME" \
        "$ALPINE_GLIBC_BIN_PACKAGE_FILENAME" \
        "$ALPINE_GLIBC_I18N_PACKAGE_FILENAME" && \
    \
    rm "/etc/apk/keys/sgerrand.rsa.pub" && \
    /usr/glibc-compat/bin/localedef --force --inputfile POSIX --charmap UTF-8 C.UTF-8 || true && \
    echo "export LANG=C.UTF-8" > /etc/profile.d/locale.sh && \
    \
    apk del glibc-i18n && \
    apk del .build-dependencies && \
    rm \
        "$ALPINE_GLIBC_BASE_PACKAGE_FILENAME" \
        "$ALPINE_GLIBC_BIN_PACKAGE_FILENAME" \
        "$ALPINE_GLIBC_I18N_PACKAGE_FILENAME"

ENV LANG=C.UTF-8

RUN mkdir -p /opt/csplogs/

ENV JAVA_VERSION=8 JAVA_UPDATE=131 JAVA_BUILD=11 JAVA_PATH=d54c1d3a095b4ff2b6607d096fa80163 JAVA_HOME=/usr/lib/jvm/default-jvm
RUN apk add --no-cache --virtual=build-dependencies wget ca-certificates unzip && update-ca-certificates && cd "/tmp" \
   && wget "http://central.preprod.melicertes.eu/repo-loads/java8/jdk-8u131-linux-x64.tar.gz" \
   && tar -xzf "jdk-${JAVA_VERSION}u${JAVA_UPDATE}-linux-x64.tar.gz" && mkdir -p "/usr/lib/jvm"\
   && mv "/tmp/jdk1.${JAVA_VERSION}.0_${JAVA_UPDATE}" "/usr/lib/jvm/java-${JAVA_VERSION}-oracle" && ln -s "java-${JAVA_VERSION}-oracle" "$JAVA_HOME" \
   && ln -s "$JAVA_HOME/bin/"* "/usr/bin/" && rm -rf "$JAVA_HOME/"*src.zip \
   && rm -rf "$JAVA_HOME/lib/missioncontrol" "$JAVA_HOME/lib/visualvm" "$JAVA_HOME/lib/"*javafx* "$JAVA_HOME/jre/lib/plugin.jar" "$JAVA_HOME/jre/lib/ext/jfxrt.jar" "$JAVA_HOME/jre/bin/javaws" "$JAVA_HOME/jre/lib/javaws.jar" "$JAVA_HOME/jre/lib/desktop" "$JAVA_HOME/jre/plugin" "$JAVA_HOME/jre/lib/"deploy* "$JAVA_HOME/jre/lib/"*javafx* "$JAVA_HOME/jre/lib/"*jfx* "$JAVA_HOME/jre/lib/amd64/libdecora_sse.so" "$JAVA_HOME/jre/lib/amd64/"libprism_*.so "$JAVA_HOME/jre/lib/amd64/libfxplugins.so" "$JAVA_HOME/jre/lib/amd64/libglass.so" "$JAVA_HOME/jre/lib/amd64/libgstreamer-lite.so" "$JAVA_HOME/jre/lib/amd64/"libjavafx*.so "$JAVA_HOME/jre/lib/amd64/"libjfx*.so\
   && wget "http://central.preprod.melicertes.eu/repo-loads/java8/jce_policy-8.zip" \
   && unzip -jo -d "${JAVA_HOME}/jre/lib/security" "jce_policy-${JAVA_VERSION}.zip" && rm "${JAVA_HOME}/jre/lib/security/README.txt" && apk del build-dependencies && rm "/tmp/"*

RUN apk add --no-cache bash drill wget ca-certificates libressl python py2-pip py-jinja2 git \
   && pip install --upgrade pip \
   && pip install j2cli[yaml] && update-ca-certificates
RUN pip install docker-compose



COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh / # backwards compat
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]
