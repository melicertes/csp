package com.intrasoft.csp.vcb.commons.model;

import java.time.ZonedDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.intrasoft.csp.vcb.commons.constants.EmailTemplateType;

@Entity
@Table(name = "vcb_emailtemplate",uniqueConstraints={@UniqueConstraint(columnNames={"name","user_id"})})
public class EmailTemplate {
	@Id
	@GeneratedValue
	private Long id;

	@NotBlank
	private String name;

	@NotNull
	private String subject;

	@NotNull
	//@Lob
	@Column(name = "content", length = 10240)
	private String content;

	@NotNull
	private Boolean active;

	@NotNull
	private ZonedDateTime modified;

	@NotNull
//	@ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public EmailTemplate() {
		this.active = false;
		this.setModified(ZonedDateTime.now());
	}

	public EmailTemplate(String name) {
		this.setName(name);
		this.active = false;
		this.setModified(ZonedDateTime.now());
	}

	public EmailTemplate(String name, Boolean active) {
		this(name);
		this.active = active;
	}

	public ZonedDateTime getModified() {
		return modified;
	}

	public void setModified(ZonedDateTime modified) {
		this.modified = modified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
