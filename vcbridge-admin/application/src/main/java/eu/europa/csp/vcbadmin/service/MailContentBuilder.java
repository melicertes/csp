package eu.europa.csp.vcbadmin.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.messageresolver.SpringMessageResolver;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;

import eu.europa.csp.vcbadmin.config.StaticTemplateExecutor;

@Service
public class MailContentBuilder {
	private static final Logger log = LoggerFactory.getLogger(MailContentBuilder.class);
	// private TemplateEngine templateEngine;

	@Autowired
	public MailContentBuilder(TemplateEngine templateEngine) {
		// this.templateEngine = templateEngine;
		// this.templateEngine.setre
	}

	@Autowired
	MessageSource messageSource;

	public String build(String emailTemplate, Map<String, String> values,boolean html) {
		SpringMessageResolver springMessageResolver = new SpringMessageResolver();
		springMessageResolver.setMessageSource(messageSource);
		Context context = new Context();
		context.setVariables(values);
		// context.setVariable("message", message);

		StaticTemplateExecutor templateEngine = new StaticTemplateExecutor(context, springMessageResolver,
				html?StandardTemplateModeHandlers.HTML5.getTemplateModeName():StandardTemplateModeHandlers.HTML5.getTemplateModeName());
		String result = templateEngine.processTemplateCode(emailTemplate);
		log.info("Prepared text for email:\n {}", result);
		return result;// , context);
	}

}