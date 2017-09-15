Central:
--------

TC: CSP::ALL, uuid: 142d6203-40cf-4e62-bc36-1a60216e941a
Team: demo2, uuid: ffb94b58-6f52-400c-92b6-6107ec6335e9

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=142d6203-40cf-4e62-bc36-1a60216e941a -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=142d6203-40cf-4e62-bc36-1a60216e941a -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo1-csp \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=142d6203-40cf-4e62-bc36-1a60216e941a -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2



Demo1:
------

TC: vulnerability, uuid: 74e8f93c-0d01-4b12-8795-1813ab725766
Team: demo2, uuid: ffb94b58-6f52-400c-92b6-6107ec6335e9

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=74e8f93c-0d01-4b12-8795-1813ab725766 -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=74e8f93c-0d01-4b12-8795-1813ab725766 -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo2-csp \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=74e8f93c-0d01-4b12-8795-1813ab725766 -DextTeamId=ffb94b58-6f52-400c-92b6-6107ec6335e9 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2


curl commands on demo1:

curl -v -s -k --key /opt/ssl/server/csp-internal.key --cert /opt/ssl/server/csp-internal.crt -H "Content-Type: application/json" -X POST -d @artefact.json https://integration.local.demo1-csp.athens.intrasoft-intl.private/v1/dsl/integrationData




Demo2:
------

TC: NIS_BASIC, uuid: 2883f242-3e07-4378-9091-0d198e4886ba
Team: demo1, uuid: 578c0e4e-ebaf-455b-a2a1-faffb14be9e1

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=2883f242-3e07-4378-9091-0d198e4886ba -DextTeamId=578c0e4e-ebaf-455b-a2a1-faffb14be9e1 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=2883f242-3e07-4378-9091-0d198e4886ba -DextTeamId=578c0e4e-ebaf-455b-a2a1-faffb14be9e1 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo1-csp \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=2883f242-3e07-4378-9091-0d198e4886ba -DextTeamId=578c0e4e-ebaf-455b-a2a1-faffb14be9e1 \
integration-tests-0.2.0-SNAPSHOT.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2

