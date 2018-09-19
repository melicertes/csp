# openam-auth-cert2

## About
*An OpenAM Custom Authentication Module*

For instructions on using custom authentication
module with OpenAM see the chapter,
*[Customizing Authentication Modules](http://openam.forgerock.org/doc/bootstrap/dev-guide/#sec-auth-spi)*,
in the OpenAM *Developer's Guide*.

This branch is for building a custom module with OpenAM 13.0.x.

* * *

The contents of this file are subject to the terms of the Common Development and
Distribution License (the License). You may not use this file except in compliance with the
License.

You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
specific language governing permission and limitations under the License.

When distributing Covered Software, include this CDDL Header Notice in each file and include
the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
Header, with the fields enclosed by brackets [] replaced by your own identifying
information: "Portions copyright [year] [name of copyright owner]".

Copyright 2013-2015 ForgeRock AS.

Copyright 2018 Fraunhofer Fokus

* * *

## Typical steps for build and deployment
Assumptions:
- openam has been installed using "openam" web context

Build and deployment steps:

1. build the jar: mvn install

2. copy jar to oam web directory: e.g. cp target/openam-auth-cert2-13.0.0.jar $CATALINA_HOME/webapps/openam/WEB-INF/lib/

3. create a Cert2.xml file: touch $CATALINA_HOME/webapps/openam/config/auth/default/Cert2.xml

4. restart the openam

5. create a new oam service: ssoadm create-svc --adminid amadmin --password-file pwd.txt --xmlfile src/main/resources/amAuthCert2.xml

6. register new authentication module: ssoadm register-auth-module --adminid amadmin --password-file pwd.txt --authmodule com.fraunhofer.fokus.csp.oam.auth.Cert2


