# Jitsi Meet

Jitsi Meet is an audio/video conferencing software based on XMPP, Jitsi
Videobridge and lots of great sofware, available at
https://github.com/jitsi/jitsi-meet

This repository contains a simple Dockerfile with overloaded configuration
to run Jitsi Meet in a single Docker container. Only exception is the
STUN server, that you might want to run separately or use a public STUN
by any service provider.

## Running Jitsi Meet

You may simply run Jitsi Meet in Docker :

```
docker run -i -t -d -p 80:80 -p 4443:4443 -p 10000:10000/udp \
  -e DOMAIN=jitsi.mydomain.com -e STUN=stun.myprovider.com \
  -e BRIDGE_IP=1.2.3.4 tedomum/jitsi
```

Or use Docker Compose :

```
[...]
services:
  jitsi:
    image: tedomum/jitsi
    ports:
      - 80:80
      - 4443:4443
      - 10000:10000/udp
```

## Exposing the service

It is highly recommended to use nginx, traefik or any popular reverse proxy
in front of Jitsi Meet, and to configure a proper TLS endpoint.

Apart from port `80` which serves the HTTP application, ports `4443` and
`10000/udp` are actually bound by the Jitsi videobridge and required to be
forwarded directly.

You may set the proper bridge IP as well as public TCP and UDP port if
different from the default ones using `BRIDGE_IP`, `BRIDGE_TCP_PORT` and
`BRIDGE_UDP_PORT`.

## Configuration

The following configuration variables are expected in the environment.

| Variable   | Required   | Purpose                    | Example            |
|------------|------------|----------------------------|--------------------|
| `DOMAIN` | yes | Public HTTP domain for the service | jitsi.mydomain.com |
| `STUN` | yes | DNS name to a STUn server | stun.myprovider.com |
| `BRIDGE_IP` | yes | Public IP exposing bridge ports | 1.2.3.4 |
| `BRIDGE_TCP_PORT` | no (4443) | Exposed bridge TCP port | 4443 |
| `BRIDGE_UDP_PORT` | no (10000) | Exposed bridge UDP port | 10000 |

## Limitations

Currently the following items are still limited:
- log management is almost non-existant
- process management could be improved
