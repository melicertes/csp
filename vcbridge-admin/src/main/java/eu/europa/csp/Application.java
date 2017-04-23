package eu.europa.csp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import eu.europa.csp.config.OpenfireProperties;

@SpringBootApplication
@ComponentScan("eu.europa.csp")
@EnableConfigurationProperties(OpenfireProperties.class)
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Application.class, args);
    }

}
