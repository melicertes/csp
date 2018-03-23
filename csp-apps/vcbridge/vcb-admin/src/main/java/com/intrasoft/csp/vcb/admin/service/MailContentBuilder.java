package com.intrasoft.csp.vcb.admin.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.messageresolver.SpringMessageResolver;

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
	TemplateEngine emailTemplateEngine;
	@Autowired
	MessageSource messageSource;

	public String build(String emailTemplate, Map<String, Object> values) {
		SpringMessageResolver springMessageResolver = new SpringMessageResolver();

		springMessageResolver.setMessageSource(messageSource);
		Context context = new Context();
		context.setVariables(values);
		// context.setVariable("message", message);
		// StaticTemplateExecutor templateEngine = new
		// StaticTemplateExecutor(context,
		// springMessageResolver,html?TemplateMode.HTML.name():TemplateMode.TEXT.name());
		// String result = templateEngine.processTemplateCode(emailTemplate);
		String result = emailTemplateEngine.process(emailTemplate, context);
		log.info("Prepared text for email:\n {}", result);
		return result;// , context);
	}

}