package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import com.intrasoft.csp.misp.service.EmitterAuditLogHandler;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import com.intrasoft.csp.misp.service.EmitterSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.io.IOException;

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MISP_ATTRIBUTE;
import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MISP_EVENT;
import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.EVENT;

@Service
public class EmitterSubscriberImpl implements EmitterSubscriber, MispContextUrl{
    final private static Logger LOG = LoggerFactory.getLogger(EmitterSubscriber.class);

    //final Logger LOG = LoggerFactory.getLogger(EmitterSubscriber.class);

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

    @Autowired
    EmitterAuditLogHandler auditLogHandler;

    @Autowired
    @Qualifier("MispAppClient")
    MispAppClient mispAppClient;

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
            String msg = subscriber.recvStr();
            LOG.debug(msg);
            String topic = msg.substring(0, msg.indexOf(' '));
            String content = msg.substring(msg.indexOf(' ') + 1);
            JsonNode jsonNode = null;
            try {
                jsonNode = new ObjectMapper().disable(SerializationFeature.INDENT_OUTPUT).readValue(content, JsonNode.class);
                boolean isDelete = false;
                boolean hasEvent = jsonNode.has("Event");

                if (jsonNode.has("Event") && jsonNode.has("action") && jsonNode.get("action").textValue().equals("delete")){
                    LOG.debug("Delete");
                    isDelete = true;
                }
                LOG.debug("Message received from queue. Topic: "  + topic);
                switch (topic){
                    case MISP_EVENT:
                        emitterDataHandler.handleMispData(jsonNode, MispEntity.EVENT, false, isDelete);
                        break;
                    case MISP_JSON_ATTRIBUTE:
                        if (jsonNode.has("Event") && jsonNode.has("action")){
                            String uuid = jsonNode.get(EVENT.toString()).get("uuid").textValue();
                            try {
                                Object object = mispAppClient.getMispEvent(uuid).getBody();
                                jsonNode = new ObjectMapper().convertValue(object, JsonNode.class);
                            }
                            catch (Exception e){
                                LOG.error("Could not fetch event, probably deleted");
                            }
                        }
                        emitterDataHandler.handleMispData(jsonNode, MispEntity.EVENT, false, isDelete);
                        break;
                    case MISP_JSON_EVENT:
                        if (jsonNode.has("Event") && jsonNode.has("action")){
                            String uuid = jsonNode.get(EVENT.toString()).get("uuid").textValue();
                            try {
                                Object object = mispAppClient.getMispEvent(uuid).getBody();
                                jsonNode = new ObjectMapper().convertValue(object, JsonNode.class);
                            }
                            catch (Exception e){
                                LOG.error("Could not fetch event, probably deleted");
                            }
                        }
                        emitterDataHandler.handleMispData(jsonNode, MispEntity.EVENT, false, isDelete);
                        break;
                    case MISP_AUDIT:
                        LOG.debug("Audit log received from queue.");
                        auditLogHandler.handleAuditLog(jsonNode);
                        break;
                    default:
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
