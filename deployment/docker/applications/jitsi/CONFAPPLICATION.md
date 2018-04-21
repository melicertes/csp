### REQUIREMENTS:
1. secure videoconferencing
2. secure chatting

Jitsi Video bridge supports both (1) and (2) with the help of 
OpenFire (v 4.1.x) which is an XMPP server
Openfire also supports - chat archiving and extracts (pdf format)
and a REST API that allows:
1. create chat room (conference room in jitsi video bridge)
2. create/delete users

So the idea is to provide a screen (OpenAM security) that allows
a user to:
*  create a new conference with a name / title-topic
* invite "outside" users (email, name should be provided)
* invite "known" users (lookup of ContactDB? for email, name)
* assign to start now or schedule for later
* have option to leave conference details for ever set (e.g. recurring)

System will either now or at scheduled time:
* create users as "participant code" and password "conference password"
* create room <roomname>
* send emails for the connection details
* track the conference and when all are left, 
   (if conference is not a recurring one)
   remove conference room and users from system
         
The email would be something like:
```
	 Hey <name>,
	 you've been invited to the conference <topic> by <user>. To 
	 connect use the following details:
	 URL : https://........../?r=<roomname>
	 UserName: <participant code>
	 Password: <conference password>
	 Thanks!
```
