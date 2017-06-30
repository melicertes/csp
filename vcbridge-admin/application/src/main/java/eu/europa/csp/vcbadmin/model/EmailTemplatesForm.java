package eu.europa.csp.vcbadmin.model;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

public class EmailTemplatesForm {
	public static class EmailTmpltForm {
		public static EmailTmpltForm fromEmailTemplate(EmailTemplate et) {
			EmailTmpltForm etf = new EmailTmpltForm();
			etf.setSubject(et.getSubject());
			etf.setContent(et.getContent());
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

		@NotBlank
		private String subject;

		@NotBlank
		private String content;
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
