package eu.europa.csp.vcbadmin.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;

public class MeetingForm {
	private static HexBinaryAdapter hba = new HexBinaryAdapter();

	public static Meeting createMeetingFromForm(MeetingForm form_meeting, User user) {
		Meeting m = new Meeting();
		m.setDuration(form_meeting.getDuration());
		m.setStart(form_meeting.getStart());
		List<Participant> participants = new LinkedList<>();
		for (ParticipantForm p : form_meeting.getEmails()) {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5"); // always create new
														// instance
														// not thread safe
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			if (md != null) {
				String hashed_email = hba.marshal(md.digest((p.getEmail() + System.currentTimeMillis()).getBytes()));
				md.reset();
				participants.add(new Participant(p.getEmail(), hashed_email.substring(0, 6), p.getName(),
						p.getSurname(), hashed_email.substring(6, 16)));
			}
		}
		m.setParticipants(participants);
		m.setStatus(MeetingStatus.Pending);
		m.setUser(user);
		m.setUid(UUID.randomUUID().toString());
		m.setSubject(form_meeting.getSubject());
		return m;
	}

	private LocalDate startDate;

	private LocalTime startTime;

	@NotNull
	private String timeZone = "Europe/Athens";

	@NotEmpty
	private String subject;

	@NotNull
	// @DateTimeFormat(iso=ISO.DATE_TIME)
	private ZonedDateTime start;

	@NotNull
	// @DateTimeFormat(iso = ISO.TIME)
	private Duration duration;

	@NotNull
	@Valid
	private LinkedList<ParticipantForm> emails = new LinkedList<>();

	public MeetingForm() {

	}

	public Duration getDuration() {
		return duration;
	}

	public LinkedList<ParticipantForm> getEmails() {
		return emails;
	}

	public ZonedDateTime getStart() {
		// if (timeZone != null)
		// return ZonedDateTime.ofInstant(start.toInstant(),
		// ZoneId.of(timeZone));
		return start;
	}

	public LocalDate getStartDate() {
		return (start == null) ? null : start.toLocalDate();
	}

	public LocalTime getStartTime() {
		return (start == null) ? null : start.toLocalTime();
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setEmails(LinkedList<ParticipantForm> emails) {
		this.emails = emails;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public String toString() {
		return "MeetingForm [start=" + start + ", duration=" + duration + ", emails=" + emails + "]";
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
