package com.intrasoft.csp.vcb.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openfire")
public class OpenfireProperties {

	/**
	 * Configuration for openfire server
	 */
	private String videobridgeHost;

	private String videobridgeEndpointPort;

	private String videobridgeAdminPort;

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

	public String getVideobridgeAdminPort() {
		return videobridgeAdminPort;
	}

	public String getVideobridgeEndpointPort() {
		return videobridgeEndpointPort;
	}

	public String getVideobridgeHost() {
		return videobridgeHost;
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

	public void setVideobridgeAdminPort(String videobridgeAdminPort) {
		this.videobridgeAdminPort = videobridgeAdminPort;
	}

	public void setVideobridgeEndpointPort(String videobridgeEndpointPort) {
		this.videobridgeEndpointPort = videobridgeEndpointPort;
	}

	public void setVideobridgeHost(String videobridgeHost) {
		this.videobridgeHost = videobridgeHost;
	}

	@Override
	public String toString() {
		return "OpenfireProperties [videobridgeHost=" + videobridgeHost + ", videobridgeEndpointPort="
				+ videobridgeEndpointPort + ", videobridgeAdminPort=" + videobridgeAdminPort + ", authUsername="
				+ authUsername + ", authPassword=" + authPassword + "]";
	}
}
