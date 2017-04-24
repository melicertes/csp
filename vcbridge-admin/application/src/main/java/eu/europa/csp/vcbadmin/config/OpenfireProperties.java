package eu.europa.csp.vcbadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openfire")
public class OpenfireProperties {

	@Override
	public String toString() {
		return "OpenfireProperties [videobridgeHost=" + videobridgeHost + ", videobridgePort=" + videobridgePort
				+ ", authUsername=" + authUsername + ", authPassword=" + authPassword + "]";
	}

	/**
	 * Configuration for openfire server
	 */
	private String videobridgeHost;

	private String videobridgePort;

	private String meetingRoom;

	private String authUsername;

	private String authPassword;

	public String getAuthPassword() {
		return authPassword;
	}

	public String getAuthUsername() {
		return authUsername;
	}

	public String getMeetingRoom() {
		return meetingRoom;
	}

	public String getVideobridgeHost() {
		return videobridgeHost;
	}
	public String getVideobridgePort() {
		return videobridgePort;
	}
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}
	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}
	public void setMeetingRoom(String meetingRoom) {
		this.meetingRoom = meetingRoom;
	}
	public void setVideobridgeHost(String videobridgeHost) {
		this.videobridgeHost = videobridgeHost;
	}

	public void setVideobridgePort(String videobridgePort) {
		this.videobridgePort = videobridgePort;
	}
}
