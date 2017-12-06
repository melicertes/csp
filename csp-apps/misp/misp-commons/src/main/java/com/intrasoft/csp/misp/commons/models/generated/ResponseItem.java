package com.intrasoft.csp.misp.commons.models.generated;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ResponseItem{

	@JsonProperty("Organisation")
	private Organisation organisation;

	@JsonProperty("editable")
	private boolean editable;

	@JsonProperty("SharingGroupServer")
	private List<SharingGroupServerItem> sharingGroupServer;

	@JsonProperty("SharingGroupOrg")
	private List<SharingGroupOrgItem> sharingGroupOrg;

	@JsonProperty("SharingGroup")
	private SharingGroup sharingGroup;

	public void setOrganisation(Organisation organisation){
		this.organisation = organisation;
	}

	public Organisation getOrganisation(){
		return organisation;
	}

	public void setEditable(boolean editable){
		this.editable = editable;
	}

	public boolean isEditable(){
		return editable;
	}

	public void setSharingGroupServer(List<SharingGroupServerItem> sharingGroupServer){
		this.sharingGroupServer = sharingGroupServer;
	}

	public List<SharingGroupServerItem> getSharingGroupServer(){
		return sharingGroupServer;
	}

	public void setSharingGroupOrg(List<SharingGroupOrgItem> sharingGroupOrg){
		this.sharingGroupOrg = sharingGroupOrg;
	}

	public List<SharingGroupOrgItem> getSharingGroupOrg(){
		return sharingGroupOrg;
	}

	public void setSharingGroup(SharingGroup sharingGroup){
		this.sharingGroup = sharingGroup;
	}

	public SharingGroup getSharingGroup(){
		return sharingGroup;
	}

	@Override
 	public String toString(){
		return 
			"ResponseItem{" + 
			"organisation = '" + organisation + '\'' + 
			",editable = '" + editable + '\'' + 
			",sharingGroupServer = '" + sharingGroupServer + '\'' + 
			",sharingGroupOrg = '" + sharingGroupOrg + '\'' + 
			",sharingGroup = '" + sharingGroup + '\'' + 
			"}";
		}
}