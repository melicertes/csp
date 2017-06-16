package com.intrasoft.csp.server.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Created by iskitsas on 4/27/17.
 */
/**
 * Overriding the default spring boot activemq starter properties
 * https://github.com/spring-projects/spring-boot/blob/v1.5.4.RELEASE/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/jms/activemq/ActiveMQProperties.java
 *
 * */

@Component
public class ActiveMQConfiguration {
//    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQConfiguration.class);
//    @Value("${activemq.broker.url}")
//    private String activemqBrokerUrl;
//
//    @Value("${activemq.username}")
//    private String activemqUsername;
//
//    @Value("${activemq.password}")
//    private String activemqPassword;
//
//    @Value("${activemq.redelivery.delay}")
//    private long redeliveryDelay;
//
//    @Value("${activemq.max.redelivery.attempts}")
//    private int maxRedeliveryAttempts;
//
//    @Value("${activemq.max.connections}")
//    private int maxConnections;
//
//    @Bean
//    public ActiveMQConnectionFactory activeMQConnectionFactory(){
//        // Connection Factory
//
//        ActiveMQConnectionFactory activeMQConnectionFactory = activeMQConnectionFactory(activemqBrokerUrl);
//        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy());
//        activeMQConnectionFactory.setTrustAllPackages(true);
//
//        return activeMQConnectionFactory;
//    }
//
//    @Bean
//    public ActiveMQComponent activemq(ActiveMQConnectionFactory activeMQConnectionFactory) {
//        // Pooled Connection Factory
//
//        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
//        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
//        pooledConnectionFactory.setMaxConnections(maxConnections);
//
//        // ActiveMQ Component
//
//        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
//        activeMQComponent.setConnectionFactory(pooledConnectionFactory);
//        activeMQComponent.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONNECTION);
//
//        return activeMQComponent;
//    }
//
//    @Bean
//    public ActiveMQComponent activemqtx(ActiveMQConnectionFactory activeMQConnectionFactory) {
//        // Pooled Connection Factory
//
//        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
//        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
//        pooledConnectionFactory.setMaxConnections(maxConnections);
//
//        // ActiveMQ Component
//
//        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
//        activeMQComponent.setConnectionFactory(pooledConnectionFactory);
//        activeMQComponent.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
////        activeMQComponent.setTransactionManager(jmsTransactionManager(activeMQConnectionFactory));
////        activeMQComponent.setTransacted(true);
//        return activeMQComponent;
//    }
//
//
//    private RedeliveryPolicy redeliveryPolicy() {
//        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
//
//        //redeliveryPolicy.setBackOffMultiplier(2);
//        //redeliveryPolicy.setUseExponentialBackOff(true);
//        redeliveryPolicy.setMaximumRedeliveries(maxRedeliveryAttempts);
//        redeliveryPolicy.setRedeliveryDelay(redeliveryDelay);
//
//        return redeliveryPolicy;
//    }
//
//    private ActiveMQConnectionFactory activeMQConnectionFactory(String brokerURL) {
//        LOG.info("SETTING UP AMQ WITH: " + brokerURL);
//        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerURL);
//        activeMQConnectionFactory.setUserName(activemqUsername);
//        activeMQConnectionFactory.setPassword(activemqPassword);
//        return activeMQConnectionFactory;
//    }
//
//    /**
//     * To support transacted endpoints
//     * http://camel.apache.org/how-do-i-make-my-jms-endpoint-transactional.html
//     * */
//    @Bean
//    public PlatformTransactionManager jmsTransactionManager(ActiveMQConnectionFactory activeMQConnectionFactory) {
//        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
//        jmsTransactionManager.setConnectionFactory(activeMQConnectionFactory);
//        return jmsTransactionManager;
//    }
}
