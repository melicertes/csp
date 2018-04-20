FROM csp-tomcat8:1.0p1-deb

MAINTAINER Majid Salehi Ghamsari <majid.salehi.ghamsari[AT]fokus[DOT]fraunhofer[DOT]de>
MAINTAINER Orestis Akrivopoulos


RUN apt-get update && \
    apt-get install -yy python bash

# setup
ENV CATALINA_OPTS="-Xmx2g -Xms2g -server"
ENV TOOLS_HOME=/opt/ssoadm
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

# download openam fraunhofer build 13 war
#RUN curl "http://central.preprod.melicertes.eu/repo-loads/openam/openam.war" -o openam.war
#RUN ls -la
#RUN mv openam.war $CATALINA_HOME/webapps/openam.war
ADD software/OpenAM-13.0.0.war $CATALINA_HOME/webapps/openam.war

# Deploy OpenAM v13.0.0 Config Tools
RUN mkdir -p $TOOLS_HOME  

ADD software/SSOConfiguratorTools-13.0.0.zip $TOOLS_HOME/SSOConfiguratorTools-13.0.0.zip

# Deploy OpenAM v13.0.0 Config Tools
RUN cd $TOOLS_HOME && \
#    curl -O "http://central.preprod.melicertes.eu/repo-loads/openam/SSOConfiguratorTools-13.0.0.zip" && \
    unzip *.zip && \
    rm SSOConfiguratorTools-13.0.0.zip

ADD software/SSOAdminTools-13.0.0.zip $TOOLS_HOME/SSOAdminTools-13.0.0.zip

# Deploy OpenAM admin Tools
RUN cd $TOOLS_HOME && \
#    curl -O "http://central.preprod.melicertes.eu/repo-loads/openam/SSOAdminTools-13.0.0.zip" && \
    unzip -o SSOAdminTools-13.0.0.zip && \
    rm SSOAdminTools-13.0.0.zip
    
ADD openam-config.properties $TOOLS_HOME/openam-config.properties
ADD agent-config.tmpl $TOOLS_HOME/agent-config.tmpl
ADD agent-config.tmpl.tc $TOOLS_HOME/agent-config.tmpl.tc
ADD agent-config.tmpl.imq $TOOLS_HOME/agent-config.tmpl.imq
ADD agent-config.tmpl.rt $TOOLS_HOME/agent-config.tmpl.rt
ADD embedded_datastore_update.properties $TOOLS_HOME/embedded_datastore_update.properties
ADD policy.json $TOOLS_HOME/policy.json
ADD policyset-rt.json $TOOLS_HOME/policyset-rt.json
ADD policyset-imq.json $TOOLS_HOME/policyset-imq.json
ADD policy-rt.json $TOOLS_HOME/policy-rt.json
ADD policy-imq.json $TOOLS_HOME/policy-imq.json
ADD run-openam.sh $TOOLS_HOME/run-openam.sh
ADD post-config-openam.sh $TOOLS_HOME/post-config-openam.sh
ADD post.batch $TOOLS_HOME/post.batch
ADD Cert.properties $TOOLS_HOME/Cert.properties
ADD setenv.sh /usr/local/tomcat/bin/setenv.sh
ADD proxy-ca-truststore.jks /usr/local/tomcat/conf/proxy-ca-truststore.jks

RUN chmod +x /usr/local/tomcat/bin/setenv.sh
RUN chmod +x $TOOLS_HOME/run-openam.sh
RUN chmod +x $TOOLS_HOME/post-config-openam.sh


COPY create-agent.sh /usr/local/bin/
RUN ln -s usr/local/bin/create-agent.sh / # backwards compat
RUN chmod +x /usr/local/bin/create-agent.sh

CMD ["/opt/ssoadm/run-openam.sh"] 



