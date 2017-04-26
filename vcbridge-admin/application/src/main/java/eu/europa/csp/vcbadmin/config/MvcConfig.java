package eu.europa.csp.vcbadmin.config;

import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.format.FormatterRegistry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import eu.europa.csp.vcbadmin.config.formatters.DurationFormatter;
import eu.europa.csp.vcbadmin.config.formatters.ZoneDateTimeFormatter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// registry.addViewController("/").setViewName("dashboard");
		// registry.addViewController("/index").setViewName("dashboard");
		// registry.addViewController("/home").setViewName("dashboard");
		registry.addViewController("/login").setViewName("login");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		// conflict with property editors
		registry.addFormatter(new DurationFormatter("HH:mm"));
		registry.addFormatter(new ZoneDateTimeFormatter(DateTimeFormatter.ISO_ZONED_DATE_TIME));
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
	
	@Bean
	 public ResourceBundleMessageSource messageSource() {
	  ResourceBundleMessageSource source = new ResourceBundleMessageSource();
	  source.setBasenames("i18n/messages");  // name of the resource bundle 
	  source.setUseCodeAsDefaultMessage(true);
	  return source;
	 }
	
	@Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        return executor;
    }
}
