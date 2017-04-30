package eu.europa.csp.vcbadmin.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;

public class MeetingForm {
	public static Meeting createMeetingFromForm(MeetingForm form_meeting, User user) {
		Meeting m = new Meeting();
		m.setDuration(form_meeting.getDuration());
		m.setStart(form_meeting.getStart());
		m.setParticipantEmails(form_meeting.getEmails());
		m.setStatus(MeetingStatus.Pending);
		m.setUser(user);
		return m;
	}

	private LocalDate startDate;

	private LocalTime startTime;

	@NotNull
	private String timeZone = "Europe/Athens";

	@NotNull
	// @DateTimeFormat(iso=ISO.DATE_TIME)
	private ZonedDateTime start;

	@NotNull
	// @DateTimeFormat(iso = ISO.TIME)
	private Duration duration;

	@NotNull
	private LinkedList<String> emails = new LinkedList<>();

	public MeetingForm() {

	}

	public Duration getDuration() {
		return duration;
	}

	public LinkedList<String> getEmails() {
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

	public void setEmails(LinkedList<String> emails) {
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

}
