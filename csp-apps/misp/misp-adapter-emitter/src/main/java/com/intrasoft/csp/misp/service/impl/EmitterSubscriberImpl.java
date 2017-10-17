package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.service.EmitterSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeromq.ZMQ;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

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
            String msg = subscriber.recvStr();
            String topic = msg.substring(0, msg.indexOf(' '));
            String content = msg.substring(msg.indexOf(' ') + 1);

            LOG.info(topic + ": " + content);

            DataParams dataParams = new DataParams();

            SharingParams sharingParams = new SharingParams();
            sharingParams.setIsExternal(true);

            IntegrationData integrationData = new IntegrationData();
            integrationData.setDataParams(dataParams);
            integrationData.setSharingParams(sharingParams);
            integrationData.setDataObject(content);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<?,?> empMap = null;
            try {
                empMap = objectMapper.readValue(new ByteArrayInputStream(content.getBytes()),Map.class);
                for (Map.Entry<?, ?> entry : empMap.entrySet())
                {
//                    logger.info("\n----------------------------\n"+entry.getKey() + "=" + entry.getValue()+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        subscriber.close ();
        context.term ();

    }
}
