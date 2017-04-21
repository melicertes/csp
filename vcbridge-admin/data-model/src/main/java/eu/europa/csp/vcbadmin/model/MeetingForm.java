package eu.europa.csp.vcbadmin.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class MeetingForm {
	public MeetingForm() {
		
	}
	public LocalDateTime getStart() {
		return start;
	}
	
	public void setStart(LocalDateTime start) {
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
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private LocalDateTime start;
	@NotNull
	@DateTimeFormat(iso=ISO.TIME)
	private Duration duration;
	@NotEmpty
	private LinkedList<String> emails=new LinkedList<>();
	@Override
	public String toString() {
		return "MeetingForm [start=" + start + ", duration=" + duration + ", emails=" + emails + "]";
	}
	
}
