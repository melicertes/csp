package com.intrasoft.csp.regrep.emailer.test;

import com.intrasoft.csp.regrep.commons.model.Mail;
import com.intrasoft.csp.regrep.emailer.service.RegularReportsMailService;
import com.intrasoft.csp.regrep.emailer.service.impl.RegularReportsMailServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {RegularReportsMailServiceImpl.class})
public class RegularReportsEmailerTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsEmailerTest.class);

    @Autowired
    RegularReportsMailService regularReportsMailService;

    @Test
    public void sendEmailTest() {

        Mail newMail = new Mail();
        newMail.setFrom("giorgosbg@boulougaris.com");
        newMail.setContent("Content");
        newMail.setSubject("Regular Reports");
        newMail.setTo("giorgosbg@outlook.com.gr");
        try {
            regularReportsMailService.sendEmail(newMail);
        } catch (MessagingException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    @Test
    public void contextLoads() {

    }

}