
https://gitlab.fokus.fraunhofer.de/EU-CSP/csp-apps.git

#to make 
mvn clean install -DskipTests

#to run this
 java -jar rt-emitter-adapter-0.4.0-SNAPSHOT-exec.jar
 
 
 REST API
 http://localhost:8081/rt/emitter/ticketid
 http://localhost:8081/rt/v1/adapter
 
 http://csp-rtemitter:8081/rt/emitter/test/3 POST PUT new Incident TEST copy from ticket nummer 3
 http://localhost:8081/rt/adapter/alltest/927 with ticket nummer 3

