package com.intrasoft.csp.conf.clientcspapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableJpaRepositories
public class ConfClientCspApplication {
	@Value("${installation.temp.directory}")
	String temp;
	@Value("${installation.modules.directory}")
	String modules;

	@EventListener
	public void applicationContextRefresh(ContextRefreshedEvent evt) throws IOException {
		if (!Files.exists(new File(temp).toPath())) {
			Files.createDirectories(new File(temp).toPath());
		}
		if (!Files.exists(new File(modules).toPath())) {
			Files.createDirectories(new File(modules).toPath());
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ConfClientCspApplication.class, args);
	}
}
