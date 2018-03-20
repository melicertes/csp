package com.intrasoft.csp.regrep.emailer.service.impl;


import com.intrasoft.csp.regrep.commons.model.Mail;
import com.intrasoft.csp.regrep.emailer.service.RegularReportsMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegularReportsMailServiceImpl implements RegularReportsMailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    Context context;

    @Value("${th.email.template}")
    String emailTemplate;
    @Value("${th.email.recipient}")
    String recipient;
    @Value("${th.email.subject}")
    String subject;
    @Value("${th.email.message}")
    String contentMessage;
    @Value("${th.email.signature}")
    String signature;

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsMailServiceImpl.class);

    @PostConstruct
    private void init() {
        context = new Context();
    }

    @Override
    public void sendEmail(Mail mail) throws MessagingException {
        LOG.info("sending email...");
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper
                (message, false, StandardCharsets.UTF_8.name());

        // Assign application properties defaults if context's variables model is null
        if (mail.getModel() == null) {
            Map defaultValuesMap = new HashMap<>();
            defaultValuesMap.put("recipient", recipient);
            defaultValuesMap.put("subject", subject);
            defaultValuesMap.put("message", contentMessage);
            defaultValuesMap.put("signature", signature);
            mail.setModel(defaultValuesMap);
        }

        context.setVariables(mail.getModel());
        final String html = templateEngine.process(emailTemplate, context);
        //String html = "<!DOCTYPE html><html><head><title>titler</title></head><body><h1>hello world!</h1></body></html>";

        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
        javaMailSender.send(message);
    }
}
