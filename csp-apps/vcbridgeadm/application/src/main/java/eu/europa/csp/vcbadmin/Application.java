package eu.europa.csp.vcbadmin;

import eu.europa.csp.vcbadmin.config.OpenfireProperties;
import eu.europa.csp.vcbadmin.config.VcbadminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan("eu.europa.csp.vcbadmin")
@EnableConfigurationProperties({ OpenfireProperties.class, VcbadminProperties.class })
@EntityScan(basePackageClasses = { Application.class, Jsr310JpaConverters.class })
@EnableScheduling
@EnableAsync
public class Application extends WebMvcConfigurerAdapter {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(Application.class, args);
	}

}