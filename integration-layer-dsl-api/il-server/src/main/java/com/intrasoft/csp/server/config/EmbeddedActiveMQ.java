package com.intrasoft.csp.server.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.apache.activemq.hooks.SpringContextHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;

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

    @Value("${embedded.activemq.persistent:false}")
    Boolean embeddedActiveMQPersistent;

    @Value("${spring.activemq.broker-url}")
    String embeddedActiveMQBrokerUrl;

    @Value("${embedded.activemq.data.path}")
    String embeddedActiveMQDataPath;

    @Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = null;
        if(embeddedActiveMQStart != null && embeddedActiveMQStart) {
            broker = new BrokerService();
            // configure the broker
            if(embeddedActiveMQDataPath != null) {
                LOG.info("-- embedded.activemq.data.path = "+embeddedActiveMQDataPath);
                broker.setDataDirectory(embeddedActiveMQDataPath);
            }
            if(embeddedActiveMQPersistent !=null){
                LOG.info("-- embedded.activemq.persistent = "+embeddedActiveMQPersistent);
                broker.setPersistent(embeddedActiveMQPersistent);
            }
            broker.addConnector(embeddedActiveMQBrokerUrl);
            broker.setShutdownHooks( Collections.singletonList( new SpringContextHook() ) );
            final ManagementContext managementContext = new ManagementContext();
            managementContext.setCreateConnector(true);
            broker.setManagementContext(managementContext);
        }
        return broker;
    }
}
