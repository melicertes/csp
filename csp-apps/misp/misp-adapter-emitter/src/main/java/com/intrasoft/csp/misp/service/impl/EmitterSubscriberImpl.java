package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.intrasoft.csp.misp.service.EmitterSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.io.IOException;

@Service
public class EmitterSubscriberImpl implements EmitterSubscriber{
    final Logger LOG = LoggerFactory.getLogger(EmitterSubscriber.class);

    @Value("${zeromq.protocol}")
    String zeroMQprotocol;

    @Value("${zeromq.host}")
    String zeroMQhost;

    @Value("${zeromq.port}")
    String zeroMQport;

    @Value("${zeromq.topic}")
    String zeroMQtopic;

    @Autowired
    EmitterDataHandler emitterDataHandler;

    @Override
    public void subscribe() {
        // Prepare our context and subscriber
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);

        String addr = zeroMQprotocol + "://" + zeroMQhost + ":" + zeroMQport;

        boolean subscribed = subscriber.connect(addr);
        LOG.info("Subscribed: " + subscribed);
        subscriber.subscribe("");
        while (!Thread.currentThread ().isInterrupted ()) {

            /*ZMsg msg2 = ZMsg.recvMsg(subscriber);
            LOG.info(String.format("Received message: %s", msg2));
            LOG.info(new String(msg2.getFirst().getData()));
            for (ZFrame frame: msg2){
                LOG.info("Frame: " + new String(frame.getData().));
            }*/
            String msg = subscriber.recvStr();
            String topic = msg.substring(0, msg.indexOf(' '));
            String content = msg.substring(msg.indexOf(' ') + 1);
            JsonNode jsonNode = null;
            try {
                jsonNode = new ObjectMapper().disable(SerializationFeature.INDENT_OUTPUT).readValue(content, JsonNode.class);
                content =jsonNode.toString();
                LOG.info(content);
            } catch (IOException e) {
                e.printStackTrace();
            }

            LOG.info(topic + ": " + jsonNode.toString());
            if (topic.equals("misp_json")){
                try {
                    LOG.info("Event message received from queue.");
                    emitterDataHandler.handleMispData(jsonNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        subscriber.close ();
        context.term ();

    }
}
