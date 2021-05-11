# CSP build instructions

## Overview
This document contains a tech-biased description and how-to for building the 
CSP components. For a detailed architectural description of what the CSP is, check
the relevant design documents in the repository, /documents folder.

## CSP components [high-level]
The CSP gets deployed in a star topology with a CENTRAL node at the middle. All satellite nodes
(referred as CSP Nodes from now on) are directly attached to CENTRAL for the purpose of 
registration and module updates. Continuous connectivity to CENTRAL is optional, however it is 
recommended. To build the CSP components, one must successfully build:
* the CSP adapter and emitter software that exists as java code in /csp-apps. Note that other 
code that resides there (python based) is used differently;
* the CSP installer;
* the module called `base` that contains the basic images which the rest are based on;
* the modules for the applications.

## Compiling
The following sections document how to compile/build the system.

### Compiling the CSP adapter emitters
The code is build via maven and JDK 8; to build you need:
* Java OpenJDK 1.8 build 212 or better;
* Apache Maven 3.5.3 or better.

Going into the /csp-apps directory, one should type the following to compile:

```shell script
mvn clean install -DskipTests
```

This will, given the required software above, complete with a "SUCCESS" message after 3-5 minutes.
The modules created are found by the following command: `find . -name "*exec.jar" ` when executed from
the /csp-apps directory.

```bash
csp-apps $ find . -name "*exec.jar"
./integration-layer/il-server/target/il-server-4.3.1-SNAPSHOT-exec.jar
./anonymization/anon-server/target/anon-server-4.3.1-SNAPSHOT-exec.jar
./intelmq/intelmq-emitter/target/intelmq-emitter-4.3.1-SNAPSHOT-exec.jar
./regular-reports/regrep-app/target/regrep-app-4.3.1-SNAPSHOT-exec.jar
./configuration/conf-server/target/conf-server-4.3.1-SNAPSHOT-exec.jar
./rt/rt-adapter-emitter/target/rt-emitter-adapter-4.3.1-SNAPSHOT-exec.jar
./misp/misp-adapter-emitter/target/misp-adapter-emitter-4.3.1-SNAPSHOT-exec.jar
./vcbridge/vcb-admin/target/vcb-admin-4.3.1-SNAPSHOT-exec.jar
./vcbridge/vcb-teleconf/target/vcb-teleconf-4.3.1-SNAPSHOT-exec.jar
```

### Compiling the base images
Going into the /deployment/docker directory, a couple of scripts exist. Use the script `01.build-base-module.sh`
to build the containers that create the base module. Then, use the script `02.make-base-module.sh` to create
the base module. Keep the base module for later use.

### Compiling the application modules
Some of the application modules _require_ an adapter/emitter. To find which ones, execute the following
command (from the `deployment/docker/applications` dir): `find . -name "docker-compose.yml" -exec fgrep -H \.jar {} \+ |grep -v command`
The produced list (see example below) shows directories and modules that require the respective target files:
```shell script
# find . -name "docker-compose.yml" -exec fgrep -H \.jar {} \+ |grep -v command
./rt/docker-compose.yml:        - ./rt-emitter-adapter-4.0.7-SNAPSHOT-exec.jar:/opt/csp/server.jar
./regularreports/docker-compose.yml:        - ./regrep-app-4.0.0-SNAPSHOT-exec.jar:/opt/csp/server.jar
./misp/docker-compose.yml:            - ./misp-server-4.0.0-SNAPSHOT-exec.jar:/opt/csp/server.jar
./intelmq/docker-compose.yml:        - ./intelmq-emitter-4.0.0-SNAPSHOT-exec.jar:/opt/csp/server.jar
./anonymization/docker-compose.yml:        - ./anon-server-exec.jar:/opt/csp/server.jar
./integrationlayer/docker-compose.yml:        - ./il-server-4.0.7-SNAPSHOT-exec.jar:/opt/csp/server.jar
./integrationlayer/docker-compose.yml:        - ./integration-tests-4.0.7-SNAPSHOT.jar:/opt/csp/itests.jar
./vcb/docker-compose.yml:        - ./vcb-admin-exec.jar:/opt/csp/server.jar
./vcb/docker-compose.yml:        - ./vcb-teleconf-exec.jar:/opt/csp/server.jar
```
In essence, the "left side" before the separator `:` is the expected "exec.jar" file. You need to 
produce/replace existing exec.jar file with the one produced in the step that compiles the code
(Compiling the CSP adapter emitters).

example: replacement/emplacement of newly create **jar** file:
```bash
csp $ cd deployment/docker/applications
applications $ cp ../../../csp-apps/integration-layer/il-server/target/il-server-4.3.1-SNAPSHOT-exec.jar ./integrationlayer/il-server-4.0.7-SNAPSHOT-exec.jar
```
according the example stdouts from above (`Compiling the CSP adapter emitters` and `Compiling the application modules`)

When all replacements are made, the script `makeModules.sh` should be executed. This will build all modules.
The script should be executed from the directory /deployment/docker/applications. It should take more than 1
hour to complete the build.

So far :- 
1. You have built the code
2. You have built the base module
3. You have built the application modules

Next steps :- building the central applications and bootstrapping central.

### Building CENTRAL

The Central node has a package that is created by the script `createInstaller.sh` that exists in 
folder `/deployment/docker/central`. *Note:* you will need to update the "VER" variable, to point to the correct
version of the artifacts (currently it is 4.3.1-SNAPSHOT as of 1/11/19). This zip file is used by the instructions
in the Central Installation document (see /documents folder.)

### Almost there
To bootstrap central, some updates are required in the relevant docker-compose file (which was packaged in 
the previous step). The document contains the necessary steps; the gist of it is that when CENTRAL is constructed
and a PKI Service exists (so certificates can be created for all services), the next steps are to configure modules
and start the CSP registrations process. A DNS service is also required to provide for naming for all services, as
they are exposed as CNAMEs on the base name of the CSP installation. 
