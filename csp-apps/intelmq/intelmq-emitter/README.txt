IntelMQ-Emitter-REST-API-Output
http://csp-imqemitter:8081/intelmq/v1/emitter
intelmqctl stop
redis-cli flushall
intelmqctl start


Try to build again:

mvn clean install -DskipTests

DD-006
Only an Emitter will be created for IntelMQ.
The CSP architecture defines the Adapter as the connection point by which an application 
can receive data via the integration layer. IntelMQ acts as an automated information 
harvester and collector, acting as a source of data to other applications. 
It does not act as an autonomous end-user application in itself and does not 
have to be enriched with data from other applications, so it does not need an Adapter.
IntelMQ only provides data to other applications via its Emitter.


ASSUMP-005
IntelMQ only sends out data to other CSP components but does not receive data from them.
The function of IntelMQ is to automatically harvest incident information from configured 
sources. It is in essence a collector of information but does not hold that information on its own.
Data collected by IntelMQ will need to be synchronized with other CSP components to make 
it functionally persistent in the applications’ workflows.
IntelMQ will not be used for incident management.
No Adapter needs to be developed for IntelMQ, only an Emitter.

ASSUMP-006
The instance of IntelMQ on the CSP will be created with barebones processing flows.
The logic of Incident collecting by IntelMQ may vary greatly between CSIRTs. It is not 
in scope of the project to provide for this variety. A barebones set of processing flows
will be supplied to serve as examples.
CSIRTs will need to configure their preferred IntelMQ processing flows on their own accord.


