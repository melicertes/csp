#!/bin/sh


echo "[i] deleting the csp-intelmq_adapter container"
docker rm csp-intelmq_emitter

echo "[i] deleting the csp-intelmq container"
docker rm csp-intelmq

echo "[i] removing the docker volumes used by intelmq"
#docker volume rm -f SomeDatavolume 


echo "[i] removing the image csp-intelmq:1.0"
docker rmi csp-intelmq:1.0

