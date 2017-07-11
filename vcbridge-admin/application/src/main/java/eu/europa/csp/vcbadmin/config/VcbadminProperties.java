package eu.europa.csp.vcbadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vcbadmin")
public class VcbadminProperties {
	public static class EmailNotifications {
		private Integer waitAfterSubmission = 10;

		private Integer minTimeAllowedBefore = 30; // ten minutes
		private Integer maxTimeAllowedBefore = 525960; // one year

		public Integer getWaitAfterSubmission() {
			return waitAfterSubmission;
		}

		public void setWaitAfterSubmission(Integer waitAfterSubmission) {
			this.waitAfterSubmission = waitAfterSubmission;
		}

		/**
		 * @return the min minutes before meeting to send invitation emails
		 */
		public Integer getMinTimeAllowedBefore() {
			return minTimeAllowedBefore;
		}

		public void setMinTimeAllowedBefore(Integer minTimeAllowedBefore) {
			this.minTimeAllowedBefore = minTimeAllowedBefore;
		}

		/**
		 * @return the max minutes before meeting to send invitation emails
		 */
		public Integer getMaxTimeAllowedBefore() {
			return maxTimeAllowedBefore;
		}

		public void setMaxTimeAllowedBefore(Integer maxTimeAllowedBefore) {
			this.maxTimeAllowedBefore = maxTimeAllowedBefore;
		}
	}

	private EmailNotifications emailNotifications;
	private Integer maxTaskRetries = 3;
	private Integer minMeetingDuration = 30; // half hour
	private Integer maxMeetingDuration = 480; // eight hours

	public Integer getMaxTaskRetries() {
		return maxTaskRetries;
	}

	public void setMaxTaskRetries(Integer maxTaskRetries) {
		this.maxTaskRetries = maxTaskRetries;
	}

	public EmailNotifications getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(EmailNotifications emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	public Integer getMinMeetingDuration() {
		return minMeetingDuration;
	}

	public void setMinMeetingDuration(Integer minMeetingDuration) {
		this.minMeetingDuration = minMeetingDuration;
	}

	public Integer getMaxMeetingDuration() {
		return maxMeetingDuration;
	}

	public void setMaxMeetingDuration(Integer maxMeetingDuration) {
		this.maxMeetingDuration = maxMeetingDuration;
	}

}
