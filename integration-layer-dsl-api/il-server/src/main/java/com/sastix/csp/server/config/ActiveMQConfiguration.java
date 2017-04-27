package com.sastix.csp.server.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Created by iskitsas on 4/27/17.
 */
@Component
public class ActiveMQConfiguration {
    @Value("${activemq.broker.url}")
    private String activemqBrokerUrl;

    @Value("${activemq.username}")
    private String activemqUsername;

    @Value("${activemq.password}")
    private String activemqPassword;

    @Value("${activemq.max.redelivery.delay}")
    private long maximumRedeliveryDelay;

    @Value("${activemq.max.retry.attempts}")
    private int maxRetryAttempts;

    @Value("${activemq.max.connections}")
    private int maxConnections;

    @Bean
    public ActiveMQComponent activemq() {
        // Connection Factory

        ActiveMQConnectionFactory activeMQConnectionFactory = activeMQConnectionFactory(activemqBrokerUrl);

        // Pooled Connection Factory

        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
        pooledConnectionFactory.setMaxConnections(maxConnections);
        activeMQConnectionFactory.setTrustAllPackages(true);

        // ActiveMQ Component

        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(pooledConnectionFactory);
        activeMQComponent.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONNECTION);

        return activeMQComponent;
    }

    @Bean
    public ActiveMQComponent activemqtx()
    {
        // Connection Factory

        ActiveMQConnectionFactory activeMQConnectionFactory = activeMQConnectionFactory(activemqBrokerUrl);
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy());
        activeMQConnectionFactory.setTrustAllPackages(true);
        // Pooled Connection Factory

        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
        pooledConnectionFactory.setMaxConnections(maxConnections);

        // ActiveMQ Component

        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(pooledConnectionFactory);
        activeMQComponent.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
//        activeMQComponent.setTransacted(true);
        return activeMQComponent;
    }


    private RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

        //redeliveryPolicy.setBackOffMultiplier(2);
        //redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setMaximumRedeliveries(maxRetryAttempts);
        redeliveryPolicy.setRedeliveryDelay(maximumRedeliveryDelay);

        return redeliveryPolicy;
    }

    private ActiveMQConnectionFactory activeMQConnectionFactory(String brokerURL) {
        System.out.println("SETTING UP AMQ WITH: " + brokerURL);
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerURL);
        activeMQConnectionFactory.setUserName(activemqUsername);
        activeMQConnectionFactory.setPassword(activemqPassword);
        return activeMQConnectionFactory;
    }
}
