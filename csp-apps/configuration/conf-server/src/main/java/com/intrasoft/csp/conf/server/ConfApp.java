package com.intrasoft.csp.conf.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

@SpringBootApplication
public class ConfApp {

    @Value("${server.file.repository}")
    String repo;
    @Value("${server.file.temp}")
    String temp;

    @EventListener
    public void handleRefresh(ContextRefreshedEvent refreshedEvent) {
        File repoFile = new File(repo);
        File tempFile = new File(temp);

        repoFile.mkdirs();
        tempFile.mkdirs();

    }

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ConfApp.class);

    public static void main(String[] args) {
        LOG.info("**** Starting CSP Configuration Server ****");
        SpringApplication.run(ConfApp.class, args);
    }

}
