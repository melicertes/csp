package eu.europa.csp.vcbadmin.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import eu.europa.csp.vcbadmin.constants.EmailTemplateType;

@Entity
@Table(name = "vcb_emailtemplate")
public class EmailTemplate {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	private String subject;

	@NotNull
	@Lob
	private String content;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	EmailTemplateType type;

	public String getContent() {
		return content;
	}

	public Long getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public EmailTemplateType getType() {
		return type;
	}

	public User getUser() {
		return user;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setType(EmailTemplateType type) {
		this.type = type;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
