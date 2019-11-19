# -----------------------------------------------------------
# Dockerfile for CSP RT + RT-IR Installation
# -----------------------------------------------------------
# Released on: 2017-11-22
# -----------------------------------------------------------
# -----------------------------------------------------------
# Updated on: 2019-11-19
# -----------------------------------------------------------

* Short description
- Perl
Version: perl 5, version 24, subversion 3 (v5.24.3) built for x86_64-linux-thread-multi

- RT
Version: 4.4.4

- RT-IR
Version: 4.0.1

- Preset users
1. root/password - superuser
2. rt-admin/password - superuser

- Custom stuff
-- Installed custom fileds:
1. [RT_UUID]
Every ticket contains an global unique identifier set while
creating the ticket.
Custom Action stored under: 
/opt/rt4/local/lib/RT/Action/SetUUIDAction.pm
2. [Sharing Policy]
At the time beeing, there is no posibility to perform manual
sharing, therefore only two options are enabled:
a) 'no sharing' (or empty) - it means, the ticket should be no shared at all at
the moment
b) 'default sharing' - meaning, the default sharing mechanism of IL
should applied while sharing is executed.

3. [Additional Metadata]
A text field, where some data can be copied to.
4. [Linked events]
A multiple values field for links to relevant events based 
in MISP.
5. [Linked threats]
A multiple values field for links to relevant threats based 
in MISP.
6. [Linked vulnerabilities]
A multiple values field for links to relevant vulnerabilities 
in TARANIS.
-- data propagation
Incident data changes will be propagated into IL:
Custom Action stored under:
/opt/rt4/local/lib/RT/Action/CSPOnCreateAction.pm
Custom Condition stored under:
/opt/rt4/local/lib/RT/Condition/CSP_ToEmitter.pm

- Authentication
A built-in mechanism supported by RT out of the box is used. 
The mechanism bases on external authentication, i our case
done by OpenAM and required a userid stored in a particular
anvironment variable REMOT_USER done by the http server (usually
done by http client authentication, but not in our case). Openam
sends userid as a part of http header, web server executes small
lua-script and copies it into correpsonding env var for RT.
In case the userid is unknown for RT the account for the id will
be automatically created and the user get the acces to RT.
Atutorization will also take place in OpenAM and based on a Policy,
that the access will be granted to only memeners of rt-user 
or/and rt-admin groups.
At the moment, there is no posibility to log in into RT by using 
the default RT-log-in-mechanism. The automatically generated
users will not have a password set, it is also not necessary.

# -----------------------------------------------------------
* Reinit of RT database
Sometimes it is required to starts with the initial database
of RT. In order to reinit the RT + RT-IR database (which 
means drop and init) execute the following steps:
# -----------------------------------------------------------

1. Shutdown the csp-rt container (otherwise there is still active connection 
to the database).
2. docker-compose run --rm --entrypoint /scripts/reinitdb.sh rt
3. docker-compose start; docker-compose logs -f

# -----------------------------------------------------------
* Upgrade RT (also remove the DB)
# -----------------------------------------------------------

1. docker-compose stop
2. ./last-time.sh
3. ./build.sh (or) load -i <new-image>
4. ./first-time.sh
5. docker-compose create
6. docker-compose start; docker-compose logs -f

# -----------------------------------------------------------
* Upgrade RT (without losing the DB data)
# -----------------------------------------------------------

1. docker-compose stop
2. ./reset-rt-cfg.sh
3. ./build.sh (or) docker load -i <new-image>
4. docker-compose create
5. docker-compose start; docker-compose logs -f


