package com.sastix.csp.server.config;

import com.sastix.csp.server.service.ErrorMessageHandler;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

/**
 * Created by iskitsas on 4/27/17.
 */

/**
* Basic usage: for integration/unit tests to test tcp://localhost:61616 broker url
*/
@Configuration
public class EmbeddedActiveMQ {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedActiveMQ.class);
    @Value("${embedded.activemq.start}")
    Boolean embeddedActiveMQStart;

    @Value("${activemq.broker.url}")
    String embeddedActivemqBrokerUrl;

    @Value("${embedded.activemq.data.path}")
    String embeddedActivemqDataPath;

    @PostConstruct
    public void init() throws Exception {
        if(embeddedActiveMQStart != null && embeddedActiveMQStart) {
            BrokerService broker = new BrokerService();
            // configure the broker
            if(embeddedActivemqDataPath != null) {
                LOG.info("-- embedded.activemq.data.path = "+embeddedActivemqDataPath);
                broker.setDataDirectory(embeddedActivemqDataPath);
            }
            broker.addConnector(embeddedActivemqBrokerUrl);
            broker.start();
        }
    }

}
