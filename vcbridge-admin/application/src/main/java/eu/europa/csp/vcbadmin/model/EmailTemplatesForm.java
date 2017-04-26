package eu.europa.csp.vcbadmin.model;

public class EmailTemplatesForm {
	public static EmailTemplatesForm fromUser(User user) {
		
		EmailTemplatesForm etf = new EmailTemplatesForm();
		etf.setInvitation(user.getInvitation());
		etf.setCancellation(user.getCancellation());
		return etf;
	}

	EmailTemplate invitation;

	public EmailTemplate getInvitation() {
		return invitation;
	}

	public void setInvitation(EmailTemplate invitation) {
		this.invitation = invitation;
	}

	EmailTemplate cancellation;

	public EmailTemplate getCancellation() {
		return cancellation;
	}

	public void setCancellation(EmailTemplate cancellation) {
		this.cancellation = cancellation;
	}
}
