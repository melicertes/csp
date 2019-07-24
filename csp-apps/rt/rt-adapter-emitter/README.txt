
https://github.com/melicertes/csp.git

#to make 
mvn clean install -DskipTests

#to run this
java -jar rt-emitter-adapter-4.0.7-SNAPSHOT-exec.jar
 
 
 REST API
 http://localhost:8081/rt/emitter/ticketid
 http://localhost:8081/rt/v1/adapter
 
 http://csp-rtemitter:8081/rt/emitter/test/3 POST PUT new Incident TEST copy from ticket nummer 3
 http://localhost:8081/rt/adapter/alltest/927 with ticket nummer 3


cd /opt/csp/modules/rtf9918a2f540b/
docker-compose stop rt-adapter
docker-compose start rt-adapter
docker exec -it csp-rt /bin/sh
wget http://csp-rt_adapter:8081/rt/adapter/alltest/22

