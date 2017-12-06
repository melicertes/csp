package com.intrasoft.csp.misp.commons.models.generated;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

public class SharingGroupServerItem{

	@JsonProperty("all_orgs")
	private boolean allOrgs;

	@JsonProperty("sharing_group_id")
	private String sharingGroupId;

	@JsonProperty("Server")
	private List<Object> server;

	@JsonProperty("server_id")
	private String serverId;

	public void setAllOrgs(boolean allOrgs){
		this.allOrgs = allOrgs;
	}

	public boolean isAllOrgs(){
		return allOrgs;
	}

	public void setSharingGroupId(String sharingGroupId){
		this.sharingGroupId = sharingGroupId;
	}

	public String getSharingGroupId(){
		return sharingGroupId;
	}

	public void setServer(List<Object> server){
		this.server = server;
	}

	public List<Object> getServer(){
		return server;
	}

	public void setServerId(String serverId){
		this.serverId = serverId;
	}

	public String getServerId(){
		return serverId;
	}

	@Override
 	public String toString(){
		return 
			"SharingGroupServerItem{" + 
			"all_orgs = '" + allOrgs + '\'' + 
			",sharing_group_id = '" + sharingGroupId + '\'' + 
			",server = '" + server + '\'' + 
			",server_id = '" + serverId + '\'' + 
			"}";
		}
}