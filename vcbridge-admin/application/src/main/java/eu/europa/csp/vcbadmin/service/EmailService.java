package eu.europa.csp.vcbadmin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.csp.vcbadmin.constants.EmailTemplateType;
import eu.europa.csp.vcbadmin.model.EmailTemplate;
import eu.europa.csp.vcbadmin.model.Meeting;
import eu.europa.csp.vcbadmin.model.Participant;

@Service
public class EmailService {
	static class MessagePreparatorImpl implements MimeMessagePreparator {
		public MessagePreparatorImpl(Meeting meeting, EmailTemplate et, MailContentBuilder mailContentBuilder,
				Participant p, String ics) {
			super();
			this.meeting = meeting;
			this.et = et;
			this.mailContentBuilder = mailContentBuilder;
			this.p = p;
			this.ics = ics;
		}

		EmailTemplate et;
		Meeting meeting;
		MailContentBuilder mailContentBuilder;
		Participant p;
		String ics;

		@Override
		public void prepare(MimeMessage mimeMessage) throws Exception {
			Map<String, Object> m = new HashMap<>();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			// messageHelper.setFrom(meeting.getUser().getEmail());
			messageHelper.setFrom("do-not-reply@sastix.com");
			messageHelper.setTo(p.getEmail());

			m.put("email", p.getEmail());
			m.put("meeting_date", meeting.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			m.put("meeting_time", meeting.getStart().format(DateTimeFormatter.ofPattern("HH:mm ZZZ")));
			m.put("meeting_username", p.getUsername());
			m.put("meeting_password", p.getPassword());
			m.put("meeting_url", meeting.getUrl());
			m.put("user_first", meeting.getUser().getFirstName());
			m.put("user_lastname", meeting.getUser().getLastName());

			String subject = mailContentBuilder.build(et.getSubject(), m);
			messageHelper.setSubject(subject);
			String content = mailContentBuilder.build(et.getContent(), m);

			messageHelper.setText(content, true);
			if (et.getType().equals(EmailTemplateType.INVITATION)) {

				messageHelper.addAttachment("meeting.ics", new ByteArrayDataSource(ics, "text/calendar"));
			}
		}

	}

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	@Autowired
	JavaMailSender mailSender;

	@Autowired
	MailContentBuilder mailContentBuilder;

	@Value(value = "classpath:templates/icalendar/meeting.ics")
	private Resource meetingTemplate;

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void prepareAndSend(EmailTemplate et, Meeting meeting) throws IOException {
		Map<String, Object> m = new HashMap<>();
		// String ics = null;

		// System.out.println("Participants!!:: " + meeting.getParticipants());
		for (Participant p : meeting.getParticipants()) {
			// MimeMessageHelper messageHelper = new MimeMessageHelper(new
			// MimeMessage((Session)(null)));

			MimeMessagePreparator messagePreparator = mimeMessage -> {
				MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
				// messageHelper.setFrom(meeting.getUser().getEmail());
				messageHelper.setFrom("do-not-reply@sastix.com");
				messageHelper.setTo(p.getEmail());

				m.put("email", p.getEmail());
				m.put("meeting_date", meeting.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				m.put("meeting_time", meeting.getStart().format(DateTimeFormatter.ofPattern("HH:mm ZZZ")));
				m.put("meeting_username", p.getUsername());
				m.put("meeting_password", p.getPassword());
				m.put("meeting_url", meeting.getUrl());
				m.put("user_first", meeting.getUser().getFirstName());
				m.put("user_lastname", meeting.getUser().getLastName());

				String subject = mailContentBuilder.build(et.getSubject(), m);
				messageHelper.setSubject(subject);
				String content = mailContentBuilder.build(et.getContent(), m);

				messageHelper.setText(content, true);

				// icalendar stuff
				BufferedReader br = new BufferedReader(new InputStreamReader(meetingTemplate.getInputStream()), 1024);
				String line;
				StringBuilder meetingICSBuilder = new StringBuilder();
				while ((line = br.readLine()) != null) {
					meetingICSBuilder.append(line).append('\n');
				}
				br.close();
				m.put("organizer", meeting.getUser().getFullName());
				m.put("email", meeting.getUser().getEmail());
				m.put("summary", subject);
				m.put("description", String.format("Participants: %s Meeting url: %s", meeting.getParticipants().size(),
						meeting.getUrl()));
				m.put("duration", meeting.getDuration().toString());
				m.put("start", DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
						.format(ZonedDateTime.ofInstant(meeting.getStart().toInstant(), ZoneOffset.UTC)));
				m.put("uid", meeting.getUid());
				m.put("location", meeting.getUrl());
				if (et.getType().equals(EmailTemplateType.INVITATION)) {
					m.put("sequence", 0);
					m.put("status", "CONFIRMED");
				} else {
					m.put("sequence", 1);
					m.put("status", "CANCELLED");
				}
				String ics = mailContentBuilder.build(meetingICSBuilder.toString(), m);
				mimeMessage.setHeader("Content-Class", "urn:content-  classes:calendarmessage");
				mimeMessage.setHeader("Content-ID", "calendar_message");
				messageHelper.addAttachment("meeting.ics", new ByteArrayDataSource(ics, "text/calendar"));
			};

			// MimeMessagePreparator messagePreparator = new
			// MessagePreparatorImpl(meeting, et, mailContentBuilder, p,
			// ics);
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