package com.intrasoft.csp.regrep.service;

import com.intrasoft.csp.regrep.commons.model.Mail;
import javax.mail.MessagingException;

public interface RegularReportsMailService {

    void sendEmail(Mail mail) throws MessagingException;

}
