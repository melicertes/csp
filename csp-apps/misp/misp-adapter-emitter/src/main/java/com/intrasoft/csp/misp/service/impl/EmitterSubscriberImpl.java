package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
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

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MISP_ATTRIBUTE;
import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MISP_EVENT;

@Service
public class EmitterSubscriberImpl implements EmitterSubscriber, MispContextUrl{
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
        LOG.info("Subscribed to " + zeroMQhost + ":" + zeroMQport + ", " + subscribed);
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
                LOG.info(topic + ": " + content);
                boolean isDelete = content.contains("\"action\":\"delete\"");
                LOG.info("isdelete: " + isDelete);
                switch (topic){
                    case MISP_EVENT:
                        LOG.info("Event message received from queue.");
                        emitterDataHandler.handleMispData(jsonNode, MispEntity.EVENT, isDelete, false);
                        break;
                    case MISP_ATTRIBUTE:
                        LOG.info("Event message received from queue.");
                        emitterDataHandler.handleMispData(jsonNode, MispEntity.ATTRIBUTE, isDelete, false);
                        break;
                    default:
//                        LOG.info("");
                        break;
                }
            } catch (IOException e) {
                LOG.error("Json serialization failed");
            }
        }
        subscriber.close ();
        context.term ();
    }
}
