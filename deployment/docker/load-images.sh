#!/bin/bash

echo "Load all CSP images (base images) "

declare -a arr=( csp-elasticsearch csp-tomcat8 csp-tomcat7 csp-python27 csp-python3 csp-postgres csp-mocknode csp-apache csp-java8 csp-alpine35glibc )
## now loop through the above array
for i in "${arr[@]}"
do
   echo "$i" 
   docker load -i "$i.tar" 
done
