FROM rmohr/activemq:5.15.6-alpine
MAINTAINER agelatos@gmail.com

COPY init.tar /opt/activemq
COPY run.sh /

WORKDIR $ACTIVEMQ_HOME
EXPOSE $ACTIVEMQ_TCP $ACTIVEMQ_AMQP $ACTIVEMQ_STOMP $ACTIVEMQ_MQTT $ACTIVEMQ_WS $ACTIVEMQ_UI

CMD [ "/bin/sh" , "/run.sh" ]
