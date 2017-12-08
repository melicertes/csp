package com.intrasoft.csp.misp.commons.models.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;

public class SharingGroupOrgItem{

	@JsonProperty("extend")
	private boolean extend;

	@JsonProperty("sharing_group_id")
	private String sharingGroupId;

	@JsonProperty("Organisation")
	private OrganisationDTO organisation;

	@JsonProperty("org_id")
	private String orgId;

	@JsonProperty("id")
	private String id;

	public void setExtend(boolean extend){
		this.extend = extend;
	}

	public boolean isExtend(){
		return extend;
	}

	public void setSharingGroupId(String sharingGroupId){
		this.sharingGroupId = sharingGroupId;
	}

	public String getSharingGroupId(){
		return sharingGroupId;
	}

	public void setOrganisation(OrganisationDTO organisation){
		this.organisation = organisation;
	}

	public OrganisationDTO getOrganisation(){
		return organisation;
	}

	public void setOrgId(String orgId){
		this.orgId = orgId;
	}

	public String getOrgId(){
		return orgId;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"SharingGroupOrgItem{" + 
			"extend = '" + extend + '\'' + 
			",sharing_group_id = '" + sharingGroupId + '\'' + 
			",organisation = '" + organisation + '\'' + 
			",org_id = '" + orgId + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}