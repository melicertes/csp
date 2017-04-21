package eu.europa.csp.vcbadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openfire")
public class OpenfireProperties {

	public String getVideobridgeHost() {
		return videobridgeHost;
	}

	public void setVideobridgeHost(String videobridgeHost) {
		this.videobridgeHost = videobridgeHost;
	}

	public String getVideobridgePort() {
		return videobridgePort;
	}

	public void setVideobridgePort(String videobridgePort) {
		this.videobridgePort = videobridgePort;
	}

	public String getMeetingRoom() {
		return meetingRoom;
	}

	public void setMeetingRoom(String meetingRoom) {
		this.meetingRoom = meetingRoom;
	}

	/**
	 * Configuration for openfire server
	 */
	private String videobridgeHost;
	private String videobridgePort;
	private String meetingRoom;
}
