
# General info on deploying misp-adapter jar on demo env #

## demo1 ##
cd /home/csp
cd misp
### take a backup of the current misp-adapter jar
cp misp-server-3.8.0-SNAPSHOT-exec.jar misp-server-3.8.0-SNAPSHOT-exec_201801301614.jar
### override current jar in use
cp /tmp/boulougaris/misp-adapter-emitter-3.8.0-SNAPSHOT-exec.jar misp-server-3.8.0-SNAPSHOT-exec.jar
### stop misp-adapter docker container
docker-compose -f docker-compose-intra.yml stop misp-adapter
### kill misp-adapter docker container (might not be needed,just in cased it didn;t properly stopped)
docker-compose -f docker-compose-intra.yml kill misp-adapter
### force docker image rm - to clear or custom configuration created in the running container and are not persisted to external volumes
docker-compose -f docker-compose-intra.yml rm -f misp-adapter
### start the container - it will use the new jar your overrode before
docker-compose -f docker-compose-intra.yml up -d misp-adapter
###see some logs to validate that it started smoothly
docker-compose -f docker-compose-intra.yml logs -f misp-adapter



## demo2 ##
cd /opt/csp/modules/misp3ed1d715ba26/
### take a backup of the current misp-adapter jar
### you might need root priveleges
su
### when prompted systempass
cp misp-server-3.8.0-SNAPSHOT-exec.jar misp-server-3.8.0-SNAPSHOT-exec_201801301614.jar
### override current jar in use
cp /tmp/boulougaris/misp-adapter-emitter-3.8.0-SNAPSHOT-exec.jar misp-server-3.8.0-SNAPSHOT-exec.jar
### stop misp-adapter docker container
docker-compose -f docker-compose.yml stop misp-adapter
### kill misp-adapter docker container (might not be needed,just in cased it didn;t properly stopped)
docker-compose -f docker-compose.yml kill misp-adapter
### force docker image rm - to clear or custom configuration created in the running container and are not persisted to external volumes
docker-compose -f docker-compose.yml rm -f misp-adapter
### start the container - it will use the new jar your overrode before
docker-compose -f docker-compose.yml up -d misp-adapter
### see some logs to validate that it started smoothly
docker-compose -f docker-compose.yml logs -f misp-adapter


## demo3 ##
cd /opt/csp/modules/misp3ed1d715ba26/
### take a backup of the current misp-adapter jar
### you might need root priveleges
su
### when prompted systempass
cp misp-server-3.8.0-SNAPSHOT-exec.jar misp-server-3.8.0-SNAPSHOT-exec_201801301614.jar
### override current jar in use
cp /tmp/boulougaris/misp-adapter-emitter-3.8.0-SNAPSHOT-exec.jar misp-server-3.8.0-SNAPSHOT-exec.jar
### stop misp-adapter docker container
docker-compose -f docker-compose.yml stop misp-adapter
### kill misp-adapter docker container (might not be needed,just in cased it didn;t properly stopped)
docker-compose -f docker-compose.yml kill misp-adapter
### force docker image rm - to clear or custom configuration created in the running container and are not persisted to external volumes
docker-compose -f docker-compose.yml rm -f misp-adapter
### start the container - it will use the new jar your overrode before
docker-compose -f docker-compose.yml up -d misp-adapter
### see some logs to validate that it started smoothly
docker-compose -f docker-compose.yml logs -f misp-adapter



# Test access to misp-ui-api with curl #

ssh demo1-csp
### connect tou misp-adapter container console
docker exec -ti csp-misp-adapter /bin/sh
### install curl if not already - for example if you remove image data with the rm -f option you have to install again curl on next connection
apk --no-cache add curl
### test if you can get the events
curl -v -s -k --key /opt/ssl/server/csp-internal.key --cert /opt/ssl/server/csp-internal.crt -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: IOXcKQDYMEDGLIOUbAbUFMYQ7WefKHj5JLaNI9iY" https://misp.local.demo1-csp.athens.intrasoft-intl.private/events


# Test misp-adapter-syncer with curl #

ssh demo1-csp
### connect tou csp-il container console
docker exec -ti csp-il /bin/sh
### install curl if not already - for example if you remove image data with the rm -f option you have to install again curl on next connection
apk --no-cache add curl
### sync organizations
curl -v -s -k --key /opt/ssl/server/csp-internal.key --cert /opt/ssl/server/csp-internal.crt https://misp-adapter.local.demo1-csp.athens.intrasoft-intl.private/misp/v1/tcSync/orgs
### sync sharing groups
curl -v -s -k --key /opt/ssl/server/csp-internal.key --cert /opt/ssl/server/csp-internal.crt https://misp-adapter.local.demo1-csp.athens.intrasoft-intl.private/misp/v1/tcSync/groups


