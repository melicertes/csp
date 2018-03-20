package com.intrasoft.csp.regrep.emailer.service;

import com.intrasoft.csp.regrep.commons.model.Mail;
import javax.mail.MessagingException;

public interface RegularReportsMailService {

    void sendEmail(Mail mail) throws MessagingException;

}
