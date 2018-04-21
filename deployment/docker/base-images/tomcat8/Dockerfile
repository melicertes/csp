FROM debian:jessie-slim
#FROM tomcat:8.0.44-jre8-alpine

MAINTAINER Thanos Angelatos

RUN apt-get update && apt-get -y install openssl tar zip gzip unzip wget curl 
ENV JAVA_VERSION=8 JAVA_UPDATE=131 JAVA_BUILD=11 JAVA_PATH=d54c1d3a095b4ff2b6607d096fa80163 JAVA_HOME=/usr/lib/jvm/default-jvm

WORKDIR /tmp
RUN wget "http://central.preprod.melicertes.eu/repo-loads/java8/jdk-8u131-linux-x64.tar.gz" \
   && tar -xzf "jdk-${JAVA_VERSION}u${JAVA_UPDATE}-linux-x64.tar.gz" && mkdir -p "/usr/lib/jvm"\
   && mv "/tmp/jdk1.${JAVA_VERSION}.0_${JAVA_UPDATE}" "/usr/lib/jvm/java-${JAVA_VERSION}-oracle" && ln -s "java-${JAVA_VERSION}-oracle" "$JAVA_HOME" \
   && ln -s "$JAVA_HOME/bin/"* "/usr/bin/" && rm -rf "$JAVA_HOME/"*src.zip \
   && rm -rf "$JAVA_HOME/lib/missioncontrol" "$JAVA_HOME/lib/visualvm" "$JAVA_HOME/lib/"*javafx* "$JAVA_HOME/jre/lib/plugin.jar" "$JAVA_HOME/jre/lib/ext/jfxrt.jar" "$JAVA_HOME/jre/bin/javaws" "$JAVA_HOME/jre/lib/javaws.jar" "$JAVA_HOME/jre/lib/desktop" "$JAVA_HOME/jre/plugin" "$JAVA_HOME/jre/lib/"deploy* "$JAVA_HOME/jre/lib/"*javafx* "$JAVA_HOME/jre/lib/"*jfx* "$JAVA_HOME/jre/lib/amd64/libdecora_sse.so" "$JAVA_HOME/jre/lib/amd64/"libprism_*.so "$JAVA_HOME/jre/lib/amd64/libfxplugins.so" "$JAVA_HOME/jre/lib/amd64/libglass.so" "$JAVA_HOME/jre/lib/amd64/libgstreamer-lite.so" "$JAVA_HOME/jre/lib/amd64/"libjavafx*.so "$JAVA_HOME/jre/lib/amd64/"libjfx*.so\
   && wget "http://central.preprod.melicertes.eu/repo-loads/java8/jce_policy-8.zip" \
   && unzip -jo -d "${JAVA_HOME}/jre/lib/security" "jce_policy-${JAVA_VERSION}.zip" && rm "${JAVA_HOME}/jre/lib/security/README.txt"

ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $CATALINA_HOME
ENV TOMCAT_MAJOR 8
## OpenAM has some problems with 8.5.x
ENV TOMCAT_VERSION 8.0.44
#
#


RUN set -x \
        && wget -O tomcat.tar.gz "https://archive.apache.org/dist/tomcat/tomcat-8/v8.0.44/bin/apache-tomcat-8.0.44.tar.gz" \
#        && wget -O tomcat.tar.gz "http://central.preprod.melicertes.eu/repo-loads/tomcat8/tomcat.tar.gz" \
        && tar -xvf tomcat.tar.gz --strip-components=1 \
        && rm bin/*.bat \
        && rm tomcat.tar.gz* 

	
EXPOSE 8080
CMD ["catalina.sh", "run"]



