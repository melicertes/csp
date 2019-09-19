package com.intrasoft.csp.vcb.admin.service;


import com.intrasoft.csp.vcb.commons.constants.EmailTemplateType;
import com.intrasoft.csp.vcb.commons.model.EmailTemplate;
import com.intrasoft.csp.vcb.commons.model.Meeting;
import com.intrasoft.csp.vcb.commons.model.Participant;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
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

import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    static {
        // fixing the cache dependency of ical4j
        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");
        // fixing an automatic attempt to update GMT
        System.setProperty("net.fortuna.ical4j.timezone.update.enabled","false");
    }


    @Value(value = "${app.mail.sender.name}")
    private String mailFromName;
    @Value(value = "${app.mail.sender.email}")
    private String mailFromMail;


    @Value(value = "${MAIL_SERVER_HOST}")
    private String MAIL_SERVER_HOST;
    @Value(value = "${MAIL_SERVER_PORT}")
    private Integer MAIL_SERVER_PORT;
    @Value(value = "${MAIL_USERNAME}")
    private String MAIL_USERNAME;
    @Value(value = "${MAIL_PASSWORD}")
    private String MAIL_PASSWORD;
    @Value(value = "${MAIL_SENDER_NAME}")
    private String MAIL_SENDER_NAME;
    @Value(value = "${MAIL_SENDER_EMAIL}")
    private String MAIL_SENDER_EMAIL;



    @Autowired
    JavaMailSender mailSender;
    @Autowired
    MailContentBuilder mailContentBuilder;
    @Value(value = "classpath:templates/icalendar/meeting.ics")
    private Resource meetingTemplate;



    public static String br2nl(String html) {
        if (html == null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false))
                .replaceAll(Pattern.quote("\\n\\n\\n\\n\\n\\n\\n\\n"), "");
    }


    @Async
    //@Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void prepareAndSend(EmailTemplate et, Meeting meeting) throws IOException {
        Map<String, Object> m = new HashMap<>();
        log.debug("Participants: " + meeting.getParticipants());
        for (Participant p : meeting.getParticipants()) {
            // MimeMessageHelper messageHelper = new MimeMessageHelper(new
            // MimeMessage((Session)(null)));

            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

                messageHelper.setFrom(new InternetAddress(mailFromMail, mailFromName));
                //messageHelper.setFrom(meeting.getUser().getEmail());

                messageHelper.setTo(p.getEmail());

                m.put("email", p.getEmail());
                m.put("meeting_date", meeting.getStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                m.put("meeting_time", meeting.getStart().format(DateTimeFormatter.ofPattern("HH:mm ZZZ")));
                m.put("meeting_duration", meeting.getDurationAsTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                m.put("meeting_duration_str", meeting.getDurationAsTime()
                        .format(DateTimeFormatter.ofPattern("H' hour(s) and 'mm' minutes'")));
                m.put("meeting_username", p.getUsername());
                m.put("meeting_password", p.getPassword());
                m.put("meeting_url", meeting.getUrl());
                m.put("meeting_subject", meeting.getSubject());

                String subject = mailContentBuilder.build(et.getSubject(), m);
                messageHelper.setSubject(subject);
                String content = mailContentBuilder.build(et.getContent(), m);

                messageHelper.setText(content, true);

                String icalendar_description = br2nl(content);//.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");// Jsoup.parse(content).text();

                TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
                TimeZone timeZone = registry.getTimeZone(meeting.getStart().getZone().getId());
                VTimeZone tz = timeZone.getVTimeZone();

                VEvent vEvent = new VEvent(new DateTime(Date.from(meeting.getStart().toInstant())),
                        new DateTime(Date.from( meeting.getExpectedEnd().toInstant())), subject);
                vEvent.getProperties().add(tz.getTimeZoneId());
                vEvent.getProperties().add(new Uid());
                vEvent.getUid().setValue(meeting.getUid());

                vEvent.getProperties().add(new Location());
                vEvent.getLocation().setValue(meeting.getUrl());

                vEvent.getProperties().add(new Description());
                vEvent.getDescription().setValue(mailContentBuilder.build(icalendar_description, m));

                //we define the organiser to be the 1st one on the list
                Participant p1 = meeting.getParticipants().get(0);

                Organizer organizer = new Organizer(URI.create("mailto:"+p1.getEmail()));
                organizer.getParameters().add(new Cn(p1.getFullname().length()==1 ? p1.getEmail() : p1.getFullname()));
                vEvent.getProperties().add(organizer);
                for (Participant part : meeting.getParticipants()) {
                    Attendee attendee = new Attendee(URI.create("mailto:"+part.getEmail()));
                    attendee.getParameters().add(Role.REQ_PARTICIPANT);
                    attendee.getParameters().add(PartStat.NEEDS_ACTION);
                    attendee.getParameters().add(new Cn(part.getFullname().length()==1 ? part.getEmail() : part.getFullname()));
                    vEvent.getProperties().add(attendee);
                }


                if (et.getType().equals(EmailTemplateType.INVITATION)) {
                    vEvent.getProperties().add(Status.VEVENT_CONFIRMED);
                    vEvent.getProperties().add(new Sequence((int)0));

                    VAlarm alarm1 = new VAlarm();
                    alarm1.getProperties().add(Action.DISPLAY);
                    alarm1.getProperties().add(new Trigger());
                    alarm1.getTrigger().setValue("-PT15M"); //15m before
                    alarm1.getProperties().add(new Description());
                    alarm1.getDescription().setValue(subject + " - " + meeting.getUrl());

                    vEvent.getAlarms().add(alarm1);

                    vEvent.getProperties().add(Transp.OPAQUE);

                } else {
                    vEvent.getProperties().add(Status.VEVENT_CANCELLED);
                    vEvent.getProperties().add(new Sequence((int)1));

                }


                //lets make the calendar at last
                net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
                icsCalendar.getProperties().add(new ProdId("-//CSP::Core Service Platform - SMART 2015_1089//vcb 1.0//EN"));
                icsCalendar.getProperties().add(Version.VERSION_2_0); // setting the version
                icsCalendar.getProperties().add(CalScale.GREGORIAN);
                icsCalendar.getProperties().add(Method.REQUEST);
                icsCalendar.getComponents().add(vEvent);
                log.info("Calendar generated: " + icsCalendar);
                mimeMessage.setHeader("Content-Class", "urn:content-classes:calendarmessage");
                messageHelper.addInline("calendar_message", new ByteArrayDataSource(icsCalendar.toString(), "text/calendar; charset=\"UTF-8\"; method=REQUEST"));
                messageHelper.addAttachment("invite.ics", new ByteArrayDataSource(icsCalendar.toString(), "application/ics"));
            };


            log.debug(MAIL_SERVER_HOST);
            log.debug(MAIL_SERVER_PORT.toString());
            log.debug(MAIL_USERNAME);
            log.debug(MAIL_PASSWORD);
            log.debug(MAIL_SENDER_NAME);
            log.debug(MAIL_SENDER_EMAIL);
            try {
                mailSender.send(messagePreparator);
                log.info("Email sent to: " + p.getEmail() + " for Meeting UID: " + meeting.getUid() + ", subject:" + meeting.getSubject());
            } catch (MailException e) {
                log.error("Error sending email to: " + p.getEmail() + " for Meeting UID: " + meeting.getUid() + ", subject:" + meeting.getSubject(),e);
            }
        }
    }

}