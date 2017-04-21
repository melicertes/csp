package eu.europa.csp.vcbadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("dashboard");
        //registry.addViewController("/hello").setViewName("hello");
		//registry.addViewController("/index").setViewName("dashboard");
		//registry.addViewController("/home").setViewName("dashboard");
        registry.addViewController("/login").setViewName("login");
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DurationConverter("HH:mm"));
        registry.addConverter(new LocalDateTimeConverter("yyyy-MM-dd'T'HH:mm"));
    }

}
