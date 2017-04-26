package eu.europa.csp.vcbadmin.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.LinkedList;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;

public class MeetingForm {
	public MeetingForm() {

	}

	public static Meeting createMeetingFromForm(MeetingForm form_meeting, User user) {
		Meeting m = new Meeting();
		m.setDuration(form_meeting.getDuration());
		m.setStart(form_meeting.getStart());
		m.setParticipantEmails(form_meeting.getEmails());
		m.setStatus(MeetingStatus.Pending);
		m.setUser(user);
		return m;
	}

	@Transient
	private String timeZone;

	public ZonedDateTime getStart() {
		// if (timeZone != null)
		// return ZonedDateTime.ofInstant(start.toInstant(),
		// ZoneId.of(timeZone));
		return start;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public LinkedList<String> getEmails() {
		return emails;
	}

	public void setEmails(LinkedList<String> emails) {
		this.emails = emails;
	}

	@NotNull
	//@DateTimeFormat(iso=ISO.DATE_TIME)
	private ZonedDateTime start;
	@NotNull
	//@DateTimeFormat(iso = ISO.TIME)
	private Duration duration;
	@NotNull
	private LinkedList<String> emails = new LinkedList<>();

	@Override
	public String toString() {
		return "MeetingForm [start=" + start + ", duration=" + duration + ", emails=" + emails + "]";
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

}
