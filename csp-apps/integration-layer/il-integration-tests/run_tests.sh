Notes:
------
New argument added to test CTC or LTC
-DtcShortNameToTest=<CTC::CSP_SHARING (default if not provided) or LTC::CSP_SHARING>

Important: Old tc uuids for short name like CSP::ALL should be replaced with CTC::CSP_SHARING uuid

Central:
--------

TC: CSP::ALL, uuid: a36c31f4-dad3-4f49-b443-e6d6333649b1
Team: demo2, uuid: af9d06ac-d7be-4684-86a3-808fe4f4d17c

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=a36c31f4-dad3-4f49-b443-e6d6333649b1 -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=a36c31f4-dad3-4f49-b443-e6d6333649b1 -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo1-csp \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=a36c31f4-dad3-4f49-b443-e6d6333649b1 -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2



Demo1:
------

TC: vulnerability, uuid: 091f38af-31cb-44ed-9953-7bc2d1e1d77f
Team: demo2, uuid: af9d06ac-d7be-4684-86a3-808fe4f4d17c

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=091f38af-31cb-44ed-9953-7bc2d1e1d77f -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=091f38af-31cb-44ed-9953-7bc2d1e1d77f -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo2-csp \
-Danon.client.ssl.jks.keystore=file:/opt/ssl/server/csp-internal.jks -Danon.client.ssl.jks.keystore.password=123456 -Danon.client.ssl.enabled=false \
-Dinternal.ssl.keystore.passphrase=123456 -Dexternal.ssl.keystore.passphrase=123456 \
-DextTcId=091f38af-31cb-44ed-9953-7bc2d1e1d77f -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2


curl commands on demo1:

curl -v -s -k --key /opt/ssl/server/csp-internal.key --cert /opt/ssl/server/csp-internal.crt -H "Content-Type: application/json" -X POST -d @artefact.json https://integration.local.demo1-csp.athens.intrasoft-intl.private/v1/dsl/integrationData




Demo2:
------

TC: NIS_BASIC, uuid: 2883f242-3e07-4378-9091-0d198e4886ba
Team: demo1, uuid: 578c0e4e-ebaf-455b-a2a1-faffb14be9e1

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-DextTcId=2883f242-3e07-4378-9091-0d198e4886ba -DextTeamId=578c0e4e-ebaf-455b-a2a1-faffb14be9e1 \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-DextTcId=2883f242-3e07-4378-9091-0d198e4886ba -DextTeamId=578c0e4e-ebaf-455b-a2a1-faffb14be9e1 \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo1-csp \
-DextTcId=2883f242-3e07-4378-9091-0d198e4886ba -DextTeamId=578c0e4e-ebaf-455b-a2a1-faffb14be9e1 \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2


Demo3:
------

TC: artefact, uuid: 5bbacdfa-920b-4db2-b282-584c444416c9
Team: demo2, uuid: af9d06ac-d7be-4684-86a3-808fe4f4d17c

java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-DextTcId=5bbacdfa-920b-4db2-b282-584c444416c9 -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1dataTypes


java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false \
-DextTcId=5bbacdfa-920b-4db2-b282-584c444416c9 -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow1verbs


java -jar -Dspring.profiles.active=docker -Dapache.camel.use.activemq=false -Dembedded.activemq.start=false -Dserver.ssl.enabled=false -DextCspId=demo2-csp \
-DextTcId=5bbacdfa-920b-4db2-b282-584c444416c9 -DextTeamId=af9d06ac-d7be-4684-86a3-808fe4f4d17c \
itests.jar \
com.intrasoft.csp.integration.business.server.internal.CspServerInternalBusinessTestFlow2

