package eu.europa.csp.vcbadmin.config;

import java.util.Collections;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
@Deprecated
public class MailConfig {
	@Configuration
	@PropertySource("classpath:mail/emailconfig.properties")
	public class SpringMailConfig implements ApplicationContextAware, EnvironmentAware {


	    @Bean
	    public ResourceBundleMessageSource emailMessageSource() {
	        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	        messageSource.setBasename("mail/MailMessages");
	        return messageSource;
	    }


	    @Bean
	    public TemplateEngine emailTemplateEngine() {
	        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	        // Resolver for TEXT emails
	        templateEngine.addTemplateResolver(textTemplateResolver());
	        // Resolver for HTML emails (except the editable one)
	        templateEngine.addTemplateResolver(htmlTemplateResolver());
	        // Resolver for HTML editable emails (which will be treated as a String)
	        templateEngine.addTemplateResolver(stringTemplateResolver());
	        // Message source, internationalization specific to emails
	        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
	        return templateEngine;
	    }

	    private ITemplateResolver textTemplateResolver() {
	        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	        templateResolver.setOrder(Integer.valueOf(1));
	        templateResolver.setResolvablePatterns(Collections.singleton("text/*"));
	        templateResolver.setPrefix("/mail/");
	        templateResolver.setSuffix(".txt");
	        templateResolver.setTemplateMode("TEXT");
	        templateResolver.setCharacterEncoding("UTF-8");
	        templateResolver.setCacheable(false);
	        return templateResolver;
	    }

	    private ITemplateResolver htmlTemplateResolver() {
	        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	        templateResolver.setOrder(Integer.valueOf(2));
	        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
	        templateResolver.setPrefix("/mail/");
	        templateResolver.setSuffix(".html");
	        templateResolver.setTemplateMode("HTML");
	        templateResolver.setCharacterEncoding("UTF-8");
	        templateResolver.setCacheable(false);
	        return templateResolver;
	    }

	    private ITemplateResolver stringTemplateResolver() {
	        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	        templateResolver.setOrder(Integer.valueOf(3));
	        // No resolvable pattern, will simply process as a String template everything not previously matched
	        templateResolver.setTemplateMode("HTML5");
	        templateResolver.setCacheable(false);
	        return templateResolver;
	    }


		@Override
		public void setEnvironment(Environment arg0) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void setApplicationContext(ApplicationContext arg0) throws BeansException {
			// TODO Auto-generated method stub
			
		}

	    //...

	}
}
