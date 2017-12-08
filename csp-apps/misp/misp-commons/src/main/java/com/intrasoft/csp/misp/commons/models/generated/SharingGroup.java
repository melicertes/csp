package com.intrasoft.csp.misp.commons.models.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;

import java.util.List;

public class SharingGroup{

	@JsonProperty("releasability")
	private String releasability;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("id")
	private String id;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("local")
	private boolean local;

	@JsonProperty("editable")
	private boolean editable;

	@JsonProperty("Organisation")
    private OrganisationDTO createdBy;

    @JsonProperty("SharingGroupOrg")
    private List<SharingGroupOrgItem> sharingGroupOrg;

    @JsonProperty("SharingGroupServer")
    private List<SharingGroupServerItem> sharingGroupServer;

	public void setReleasability(String releasability){
		this.releasability = releasability;
	}

	public String getReleasability(){
		return releasability;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isActive(){
		return active;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setLocal(boolean local){
		this.local = local;
	}

	public boolean isLocal(){
		return local;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

    public OrganisationDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(OrganisationDTO createdBy) {
        this.createdBy = createdBy;
    }

    public List<SharingGroupOrgItem> getSharingGroupOrg() {
        return sharingGroupOrg;
    }

    public void setSharingGroupOrg(List<SharingGroupOrgItem> sharingGroupOrg) {
        this.sharingGroupOrg = sharingGroupOrg;
    }

    public void addSharingGroupOrgItem(SharingGroupOrgItem sharingGroupOrgItem) {
	    sharingGroupOrg.add(sharingGroupOrgItem);
    }

    public List<SharingGroupServerItem> getSharingGroupServer() {
        return sharingGroupServer;
    }

    public void setSharingGroupServer(List<SharingGroupServerItem> sharingGroupServer) {
        this.sharingGroupServer = sharingGroupServer;
    }

    @Override
 	public String toString(){
		return 
			"SharingGroup{" + 
			"releasability = '" + releasability + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",active = '" + active + '\'' + 
			",id = '" + id + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",local = '" + local + '\'' + 
			"}";
		}
}