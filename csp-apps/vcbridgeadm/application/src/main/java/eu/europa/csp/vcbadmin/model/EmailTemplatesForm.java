package eu.europa.csp.vcbadmin.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import eu.europa.csp.vcbadmin.constants.EmailTemplateType;

public class EmailTemplatesForm {
	public static class EmailTmpltForm {
		public static EmailTmpltForm fromEmailTemplate(EmailTemplate et) {
			EmailTmpltForm etf = new EmailTmpltForm();
			etf.setSubject(et.getSubject());
			etf.setContent(et.getContent());
			etf.setActive(et.getActive());
			return etf;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Boolean getActive() {
			return active;
		}

		public void setActive(Boolean active) {
			this.active = active;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public EmailTemplateType getType() {
			return type;
		}

		public void setType(EmailTemplateType type) {
			this.type = type;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		private Long id;

		@NotNull
		private EmailTemplateType type;

		@NotBlank
		private String name;

		@NotBlank
		private String subject;

		@NotBlank
		private String content;

		@NotNull
		private Boolean active;
	}

	public static EmailTemplatesForm fromUser(User user) {
		EmailTemplatesForm etf = new EmailTemplatesForm();

		etf.setInvitation(EmailTmpltForm.fromEmailTemplate(user.getInvitation()));
		etf.setCancellation(EmailTmpltForm.fromEmailTemplate(user.getCancellation()));
		return etf;
	}

	@Valid
	EmailTmpltForm invitation;
	@Valid
	EmailTmpltForm cancellation;

	public EmailTmpltForm getCancellation() {
		return cancellation;
	}

	public EmailTmpltForm getInvitation() {
		return invitation;
	}

	public void setCancellation(EmailTmpltForm cancellation) {
		this.cancellation = cancellation;
	}

	public void setInvitation(EmailTmpltForm invitation) {
		this.invitation = invitation;
	}
}
