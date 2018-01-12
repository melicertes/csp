package com.intrasoft.csp.misp.commons.models.generated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrasoft.csp.misp.commons.models.generated.Server;

import java.util.List;


public class SharingGroupServerItem{

	@JsonProperty("all_orgs")
	private boolean allOrgs;

	@JsonProperty("sharing_group_id")
	private String sharingGroupId;

	@JsonProperty("Server")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<Server> server;

	@JsonProperty("id")
	private String id;

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

	public void setServer(List<Server> server){
		this.server = server;
	}

	public List<Server> getServer(){
		return server;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
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
			",id = '" + id + '\'' +
			",server_id = '" + serverId + '\'' + 
			"}";
		}
}