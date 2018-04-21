Container is running a Java 8 with Tomcat 8 for OpenAM 13

Docker Public Repository https://hub.docker.com/r/majidsalehi/openam/

Email:<a href="mailto:majid.salehi.ghamsari@fokus.fraunhofer.de">Majid Salehi</a> 

The new **csp-openam:1.0-Alpha** uses Configuration and upgrade tools, alternatives to using the GUI configuration wizard.

Please use this container as follow:

*docker pull majidsalehi/csp-openam:1.0-Alpha*

*docker run -h openam.csp.com -it majidsalehi/csp-openam:1.0-Alpha /bin/sh*

*#/usr/local/tomcat/bin/catalina.sh start*

*#cd /opt/ssoadm/*

 - Edit openam-config.properties 
	- search&replace openam host name default is -> **openam**
	- search&replace openam domain name default is ->**.csp.com** 
	- default amadmin password is **11111111**, you can change this also later in Admin Dashboard.
	
 - Edit agent-config 
	 - search&replace openam domain name default is **openam.csp.com**
	 - Edit your agent uri default is **www.csp.com** 	
	 - Edit your agent.logout.url default is**www.csp.com:80/api/v1/teams/logout.jsp** 	
	 - default password for webagent is **password** do not change this in config file, you can
   change this later in Admin Dashboard.


*#./post-config-openam.sh*
