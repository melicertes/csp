java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -DextCspId=demo1-csp \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2