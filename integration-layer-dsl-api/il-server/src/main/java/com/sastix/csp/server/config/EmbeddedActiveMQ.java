package com.sastix.csp.server.config;

import org.apache.activemq.broker.BrokerService;
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

    @Value("${embedded.activemq.start}")
    Boolean embeddedActiveMQStart;

    @Value("${activemq.broker.url}")
    String embeddedActivemqBrokerUrl;

    @PostConstruct
    public void init() throws Exception {
        if(embeddedActiveMQStart != null && embeddedActiveMQStart) {
            BrokerService broker = new BrokerService();
            // configure the broker
            broker.addConnector(embeddedActivemqBrokerUrl);
            broker.start();
        }
    }

}
