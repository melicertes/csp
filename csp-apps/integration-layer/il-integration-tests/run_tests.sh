java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=142d6203-40cf-4e62-bc36-1a60216e941a -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=142d6203-40cf-4e62-bc36-1a60216e941a -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -DextCspId=demo1-csp \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=142d6203-40cf-4e62-bc36-1a60216e941a -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2