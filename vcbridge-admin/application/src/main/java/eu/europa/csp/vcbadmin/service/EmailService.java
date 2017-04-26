package eu.europa.csp.vcbadmin.service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europa.csp.vcbadmin.model.EmailTemplate;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.Participant;

@Service
public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	@Autowired
	JavaMailSender mailSender;

	@Autowired
	MailContentBuilder mailContentBuilder;

	@Async
	public void prepareAndSend(EmailTemplate et, Meeting meeting) {
		// System.out.println("Participants!!:: " + meeting.getParticipants());
		for (Participant p : meeting.getParticipants()) {
			Map<String, String> m = new HashMap<>();
			// MimeMessageHelper messageHelper = new MimeMessageHelper(new
			// MimeMessage((Session)(null)));

			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
				// messageHelper.setFrom(meeting.getUser().getEmail());
				messageHelper.setFrom("noreply@allaboutcar.com.cy");
				messageHelper.setTo(p.getEmail());

				m.put("email", p.getEmail());
				m.put("meeting_date", meeting.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				m.put("meeting_time", meeting.getStart().format(DateTimeFormatter.ofPattern("HH:mm ZZZ")));
				m.put("meeting_username", p.getUsername());
				m.put("meeting_password", p.getPassword());
				m.put("user_first", meeting.getUser().getFirstName());
				m.put("user_lastname", meeting.getUser().getLastName());

				String subject = mailContentBuilder.build(et.getSubject(), m, false);
				messageHelper.setSubject(subject);

				String content = mailContentBuilder.build(et.getContent(), m, true);

				messageHelper.setText(content, true);
			};
			try {
				mailSender.send(messagePreparator);
			} catch (MailException e) {
				log.error("Error sending email to " + p.getEmail(), e);
				// runtime exception; compiler will not force you to handle it
			}
			log.info("Email sent to {}", p.getEmail());
		}
	}
}