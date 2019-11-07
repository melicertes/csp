#!/bin/bash

echo "Save all CSP images (base images) to tar for packaging"


docker pull mariadb 
docker pull node:8.16.0-alpine
declare -a arr=( mariadb node:8.16.0-alpine csp-alpine35glibc csp-java8 csp-docker-java8 csp-apache csp-postgres csp-python27 csp-tomcat8 ) ## now loop through the above array
for i in "${arr[@]}"
do
   echo "$i" 
   docker save -o "$i.tar" "$i"
   # compress even further
   bzip2 -9 "$i.tar"
done
echo '{"format":1.0}' > manifest.json
zip -9 csp-basemodule-`date '+%Y-%m-%dT%H%M'`.zip manifest.json *bz2
rm manifest.json
