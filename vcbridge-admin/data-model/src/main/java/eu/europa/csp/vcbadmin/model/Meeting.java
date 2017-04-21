package eu.europa.csp.vcbadmin.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

@Entity
@Table(name = "vcb_meeting")
public class Meeting {
	private static HexBinaryAdapter hba = new HexBinaryAdapter();
	@Id
	@GeneratedValue
	private Long id;

	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
	private List<Participant> participants = new LinkedList<>();

	@NotNull
	private String room;

	@NotNull
	private String url;

	@NotNull
	private LocalDateTime start;

	@NotNull
	private Duration duration;
	
	@NotNull
	private String status;

	public Meeting() {

	}

	public Meeting(MeetingForm form_meeting, String url) {
		this.duration = form_meeting.getDuration();
		this.start = form_meeting.getStart();
		this.setParticipantEmails(form_meeting.getEmails().stream().filter(s->s!=null && !s.isEmpty()).collect(Collectors.toList()));
		this.url=url;
		this.status="Pending";
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
				participants.add(new Participant(email, hashed_email.substring(0, 6), hashed_email.substring(6, 16),this));
				sb.append(email);
			}
			sb.append(System.currentTimeMillis());
			room = hba.marshal(md.digest(sb.toString().getBytes())).substring(0, 16);
		}
	}

	@Override
	public String toString() {
		return "Meeting [id=" + id + ", participants=" + participants + ", room=" + room + ", url=" + url + ", start="
				+ start + ", duration=" + duration + "]";
	}

	public Duration getDuration() {
		return duration;
	}

	public LocalDateTime getExpectedEnd() {
		return start.plus(duration);
	}

	public Long getId() {
		return id;
	}

	public String getRoom() {
		return room;
	}

	public LocalDateTime getStart() {
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

	public void setStart(LocalDateTime start) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}