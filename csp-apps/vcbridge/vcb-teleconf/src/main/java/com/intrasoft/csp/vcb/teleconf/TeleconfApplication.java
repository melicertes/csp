package com.intrasoft.csp.vcb.teleconf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan({"com.intrasoft.csp.vcb.teleconf, com.intrasoft.csp.vcb.commons"})
@EntityScan("com.intrasoft.csp.vcb.commons.model")
@EnableJpaRepositories(basePackages = {"com.intrasoft.csp.vcb.teleconf.repository"})
public class TeleconfApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeleconfApplication.class, args);
	}
}
