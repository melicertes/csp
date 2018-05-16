package com.intrasoft.csp.regrep.service.impl;


import com.intrasoft.csp.regrep.commons.model.Mail;
import com.intrasoft.csp.regrep.service.RegularReportsMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class RegularReportsMailServiceImpl implements RegularReportsMailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

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

    public RegularReportsMailServiceImpl() {

    }

    @PostConstruct
    private void init() {
        LOG.info("**** Initializing Regular Reports Mail Service ****");
    }

    @Override
    public void sendEmail(Mail mail) throws MessagingException {
        LOG.info("sending email...");
        Context context = new Context();
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

        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setSubject(mail.getSubject());
        if(!StringUtils.isEmpty(mail.getFrom())){
            helper.setFrom(mail.getFrom());
        }else{
            String mailFromName = mail.getSenderName();
            String mailFromMail = mail.getSenderEmail();
            try {
                helper.setFrom(new InternetAddress(mailFromMail, mailFromName));
            } catch (UnsupportedEncodingException e) {
                LOG.error("Could not set 'from' field.",e);
            }
        }


        try {
            javaMailSender.send(message);
        } catch (MailAuthenticationException e) {
            LOG.error(e.getMessage());
        }

    }
}
