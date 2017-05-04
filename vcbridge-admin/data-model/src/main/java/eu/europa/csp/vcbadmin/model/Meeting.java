package eu.europa.csp.vcbadmin.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.hibernate.validator.constraints.NotBlank;

import eu.europa.csp.vcbadmin.constants.MeetingStatus;

@Entity
@Table(name = "vcb_meeting")
public class Meeting {
	private static HexBinaryAdapter hba = new HexBinaryAdapter();
	@Id
	@GeneratedValue
	private Long id;

	private String uid;

	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Participant> participants = new LinkedList<>();

	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<MeetingScheduledTask> scheduledTasks = new LinkedList<>();

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@NotNull
	private String room;

	@NotNull
	private String url;

	@NotBlank
	private String subject;

	@NotNull
	private ZonedDateTime start;

	@NotNull
	private Duration duration;

	@NotNull
	@Enumerated(EnumType.STRING)
	private MeetingStatus status;

	public Meeting() {

	}

	public void setParticipantEmails(Collection<String> emails) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5"); // always create new instance
													// not thread safe
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (md != null) {
			StringBuilder sb = new StringBuilder();
			for (String email : emails) {
				String hashed_email = hba.marshal(md.digest((email + System.currentTimeMillis()).getBytes()));
				md.reset();
				participants
						.add(new Participant(email, hashed_email.substring(0, 6), hashed_email.substring(6, 16), this));
				sb.append(email);
			}
			sb.append(System.currentTimeMillis());
			room = hba.marshal(md.digest(sb.toString().getBytes())).substring(0, 16);
		}
	}

	@Override
	public String toString() {
		return "Meeting [id=" + id + ", room=" + room + ", url=" + url + ", start=" + start + ", duration=" + duration
				+ "]";
	}

	public Duration getDuration() {
		return duration;
	}

	public ZonedDateTime getExpectedEnd() {
		return start.plus(duration);
	}

	public Long getId() {
		return id;
	}

	public String getRoom() {
		return room;
	}

	public ZonedDateTime getStart() {
		return start;
	}

	public String getUrl() {
		return url;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public MeetingStatus getStatus() {
		return status;
	}

	public void setStatus(MeetingStatus status) {
		this.status = status;
	}

	public List<MeetingScheduledTask> getScheduledTasks() {
		return scheduledTasks;
	}

	public void setScheduledTasks(List<MeetingScheduledTask> scheduledTasks) {
		this.scheduledTasks = scheduledTasks;
	}

	public LocalTime getDurationAsTime() {
		// according to https://jira.sastix.com/browse/SXCSP-125
		// duration should be 0:30 up to 08:00, so it is safe to assume that
		// this is is a time
		// System.out.println(duration.toHours());
		// System.out.println(duration.toMinutes() % 60);
		return LocalTime.of(Long.valueOf(duration.toHours()).intValue(),
				Long.valueOf(duration.toMinutes()).intValue() % 60);
	}

	public void addScheduledTask(MeetingScheduledTask meeting_scheduled_task) {
		if (scheduledTasks == null) {
			this.scheduledTasks = new LinkedList<>();
		}
		scheduledTasks.add(meeting_scheduled_task);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}