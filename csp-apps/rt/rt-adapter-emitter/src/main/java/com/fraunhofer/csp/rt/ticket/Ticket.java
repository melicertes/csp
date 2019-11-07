package com.fraunhofer.csp.rt.ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fraunhofer.csp.rt.app.IncidentCustomFields;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public class Ticket {

	private static final Logger LOG = LoggerFactory.getLogger(Ticket.class);

	@Key("id")
	private String id;
	@Key("Queue")
	private String queue;
	@Key("Owner")
	private String owner;
	@Key("Creator")
	private String creator;
	@Key("Subject")
	private String subject;
	@Key("Status")
	private TicketStatus status;
	@Key("Requestors")
	private List<String> requestors;
	@Key("Cc")
	private List<String> cc;
	@Key("AdminCc")
	private List<String> adminCc;
	@Key("Created")
	private LocalDateTime created;
	@Key("Starts")
	private LocalDateTime starts;
	@Key("Started")
	private LocalDateTime started;
	@Key("Due")
	private LocalDateTime due;
	@Key("Resolved")
	private LocalDateTime resolved;
	@Key("Told")
	private LocalDateTime lastContact;
	@Key("LastUpdated")
	private LocalDateTime lastUpdated;
	@Key("TimeEstimated")
	private Duration timeEstimated;
	@Key("TimeWorked")
	private Duration timeWorked;
	@Key("TimeLeft")
	private Duration timeLeft;
	@Key("CF.{%s}")
	private Map<String, String> customFields;
	@Key("Links")
	private List<Ticket> links;

	@Key("Message")
	private String message;

	@Key("Comments")
	private List<String> comments;

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public Ticket() {
		this.customFields = new HashMap<>();
		this.links = new ArrayList<Ticket>();
	}

	public String getId() {
		return id.substring(id.indexOf('/') + 1);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getQueue() {
		return queue;
	}

	public String getOwner() {
		return owner;
	}

	public String getCreator() {
		return creator;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public List<String> getRequestors() {
		return requestors;
	}

	public List<String> getCc() {
		return cc;
	}

	public List<String> getAdminCc() {
		return adminCc;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public LocalDateTime getStarts() {
		return starts;
	}

	public LocalDateTime getStarted() {
		return started;
	}

	public LocalDateTime getDue() {
		return due;
	}

	public LocalDateTime getResolved() {
		return resolved;
	}

	public LocalDateTime getLastContact() {
		return lastContact;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public Duration getTimeEstimated() {
		return timeEstimated;
	}

	public Duration getTimeWorked() {
		return timeWorked;
	}

	public Duration getTimeLeft() {
		return timeLeft;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (Objects.isNull(other) || getClass() != other.getClass()) {
			return false;
		}

		final Ticket ticket = (Ticket) other;
		return Objects.equals(id, ticket.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return String.format("%s: %s", getId(), subject);
	}

	@SuppressWarnings("unchecked")
	public void setSharing(String sharing) {
		@SuppressWarnings("rawtypes")
		Map cfmap = getCustomFields();
		String key = IncidentCustomFields.CF_SHARING_POLICY.toString();
		if (sharing.equalsIgnoreCase(CfSharing.DEFAULT_SHARING.toString()))
			cfmap.put(key, CfSharing.DEFAULT_SHARING.toString());
		else
			cfmap.put(key, CfSharing.NO_SHARING.toString());
		this.customFields = cfmap;
	}

	@SuppressWarnings("unchecked")
	public void setUUID(String uuid) {
		@SuppressWarnings("rawtypes")
		Map cfmap = getCustomFields();
		String key = IncidentCustomFields.CF_RT_UUID.toString();

		cfmap.put(key, uuid);
		this.customFields = cfmap;
	}

	public String getSharing() {
		String sharing = "";
		String sp = null;
		sp = getCustomField(IncidentCustomFields.CF_SHARING_POLICY);
		LOG.trace("getCustomField(IncidentCustomFields.CF_SHARING_POLICY): {}", getCustomField(IncidentCustomFields.CF_SHARING_POLICY));
		if (sp.isEmpty() || sp.equalsIgnoreCase(CfSharing.NO_SHARING.toString())) {
			LOG.debug("CustomField Sharing policy:NO_SHARING:" + sp);
			sharing = CfSharing.NO_SHARING.toString();
		} else if (sp.equalsIgnoreCase(CfSharing.DEFAULT_SHARING.toString())) {
			LOG.debug("CustomField Sharing policy:DEFAULT_SHARING:" + sp);
			sharing = CfSharing.DEFAULT_SHARING.toString();
		} else {
			LOG.debug("CustomField Sharing policy:TCSandTEAMS:" + sp);
			sharing = sp;
		}
		return sharing;
	}

	public void addLink(final Ticket ticket) {
		if (null != ticket)
			this.links.add(ticket);
		else
			LOG.warn("got an invalid ticket as a link -> reference was null");
	}

	public void setLinks(final List<Ticket> links) {
		this.links = links;
	}

	public List<Ticket> getLinks() {
		return this.links;
	}

	public String getCustomField(IncidentCustomFields field) {
		String key = field.toString();
		Map<String, String> cfmap = getCustomFields();
		String value = "";
		if (cfmap.containsKey(key)) {
			value = cfmap.get(key).toString();
		} else
			LOG.debug("NO Key found " + key);

//		if (value == null || value.isEmpty()) {
//			LOG.debug("CustomField is null or empty " + key);
//		}
		return value;
	}

}
