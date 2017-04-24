package eu.europa.csp.vcbadmin.config;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import eu.europa.csp.vcbadmin.config.converters.DurationConverter;
import eu.europa.csp.vcbadmin.config.converters.ZoneDateTimeToStringConverter;
import eu.europa.csp.vcbadmin.config.converters.ZonedDateTimeConverter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// registry.addViewController("/").setViewName("dashboard");
		// registry.addViewController("/hello").setViewName("hello");
		// registry.addViewController("/index").setViewName("dashboard");
		// registry.addViewController("/home").setViewName("dashboard");
		registry.addViewController("/login").setViewName("login");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new DurationConverter("HH:mm"));
		registry.addConverter(new ZonedDateTimeConverter(DateTimeFormatter.ISO_ZONED_DATE_TIME)); // "yyyy-MM-dd'T'HH:mm"
		registry.addConverter(new ZoneDateTimeToStringConverter());
	}
	
	// Retry policy, used with exceptions in meeting scheduling
	@Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(5000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
 
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        retryTemplate.setRetryPolicy(retryPolicy);
         
        return retryTemplate;
    }
}
