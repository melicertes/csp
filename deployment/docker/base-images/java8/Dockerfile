FROM csp-alpine35glibc:1.0

MAINTAINER Thanos Angelatos

ENV JAVA_VERSION=8 JAVA_UPDATE=131 JAVA_BUILD=11 JAVA_PATH=d54c1d3a095b4ff2b6607d096fa80163 JAVA_HOME=/usr/lib/jvm/default-jvm
RUN apk add --no-cache --virtual=build-dependencies wget ca-certificates unzip && update-ca-certificates && cd "/tmp" \
   && wget "http://central.preprod.melicertes.eu/repo-loads/java8/jdk-8u131-linux-x64.tar.gz" \
   && tar -xzf "jdk-${JAVA_VERSION}u${JAVA_UPDATE}-linux-x64.tar.gz" && mkdir -p "/usr/lib/jvm"\
   && mv "/tmp/jdk1.${JAVA_VERSION}.0_${JAVA_UPDATE}" "/usr/lib/jvm/java-${JAVA_VERSION}-oracle" && ln -s "java-${JAVA_VERSION}-oracle" "$JAVA_HOME" \
   && ln -s "$JAVA_HOME/bin/"* "/usr/bin/" && rm -rf "$JAVA_HOME/"*src.zip \
   && rm -rf "$JAVA_HOME/lib/missioncontrol" "$JAVA_HOME/lib/visualvm" "$JAVA_HOME/lib/"*javafx* "$JAVA_HOME/jre/lib/plugin.jar" "$JAVA_HOME/jre/lib/ext/jfxrt.jar" "$JAVA_HOME/jre/bin/javaws" "$JAVA_HOME/jre/lib/javaws.jar" "$JAVA_HOME/jre/lib/desktop" "$JAVA_HOME/jre/plugin" "$JAVA_HOME/jre/lib/"deploy* "$JAVA_HOME/jre/lib/"*javafx* "$JAVA_HOME/jre/lib/"*jfx* "$JAVA_HOME/jre/lib/amd64/libdecora_sse.so" "$JAVA_HOME/jre/lib/amd64/"libprism_*.so "$JAVA_HOME/jre/lib/amd64/libfxplugins.so" "$JAVA_HOME/jre/lib/amd64/libglass.so" "$JAVA_HOME/jre/lib/amd64/libgstreamer-lite.so" "$JAVA_HOME/jre/lib/amd64/"libjavafx*.so "$JAVA_HOME/jre/lib/amd64/"libjfx*.so\
   && wget "http://central.preprod.melicertes.eu/repo-loads/java8/jce_policy-8.zip" \
   && unzip -jo -d "${JAVA_HOME}/jre/lib/security" "jce_policy-${JAVA_VERSION}.zip" && rm "${JAVA_HOME}/jre/lib/security/README.txt" && apk del build-dependencies && rm "/tmp/"*

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s usr/local/bin/docker-entrypoint.sh / # backwards compat
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["docker-entrypoint.sh"]