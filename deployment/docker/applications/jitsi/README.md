Openfire XMPP and Jitsi VideoBridge setup

To run, use docker-compose. Fix the compose script so the volumes mentioned are using the files in the "directories.zip" present here

To configure:

1. plugins: add STUN and OpenMeetings
2. configure Server -> SSL/TLS certificates (how?)
3. configure Server -> Archiving -> Archiving settings: enable chat logs for 1to1 and group
4. configure Meetings -> Settings -> 
- Video Resolution: 360
- Audio Bandwidth: 64
- Video Bandwidth: 400

- Media configuration: 
  - use ports 5000-5050 (50 udp ports) 
  - Setup proper Local address/Public address

- Advanced features: enable all simulcast and adaptive LastN


Save and restart...


