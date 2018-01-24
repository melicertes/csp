package com.intrasoft.csp.misp.commons.models.generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;

import java.util.ArrayList;
import java.util.List;

public class SharingGroup{

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
	private String name;

    @JsonProperty("releasability")
    private String releasability;

    @JsonProperty("description")
	private String description;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("local")
	private boolean local;

	@JsonProperty("editable")
	private boolean editable;

	@JsonProperty("Organisation")
    private OrganisationDTO organisation;

    @JsonProperty("SharingGroupOrg")
    private List<SharingGroupOrgItem> sharingGroupOrg;

    @JsonProperty("SharingGroupServer")
    private List<SharingGroupServerItem> sharingGroupServer;

    @JsonProperty("created")
    private String created;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("organisation_uuid")
    private String organisationUuid;

    @JsonProperty("org_id")
    private String orgId;

    @JsonProperty("sync_user_id")
    private String syncUserId;

    @JsonProperty("roaming")
    private boolean roaming;

    @JsonProperty("sync_org_name")
    private String syncOrgName;


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

    public OrganisationDTO getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationDTO organisation) {
        this.organisation = organisation;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getOrganisationUuid() {
        return organisationUuid;
    }

    public void setOrganisationUuid(String organisationUuid) {
        this.organisationUuid = organisationUuid;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSyncUserId() {
        return syncUserId;
    }

    public void setSyncUserId(String syncUserId) {
        this.syncUserId = syncUserId;
    }

    public boolean isRoaming() {
        return roaming;
    }

    public void setRoaming(boolean roaming) {
        this.roaming = roaming;
    }

    public String getSyncOrgName() {
        return syncOrgName;
    }

    public void setSyncOrgName(String syncOrgName) {
        this.syncOrgName = syncOrgName;
    }

    @JsonIgnore
    public List<OrganisationDTO> getAllOrganisations() {
	    List<OrganisationDTO> orgList = new ArrayList<>();
	    sharingGroupOrg.forEach(sgoi -> orgList.add(sgoi.getOrganisation()));
	    return orgList;
    }

    @Override
    public String toString() {
        return "SharingGroup{" +
                "releasability='" + releasability + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                ", id='" + id + '\'' +
                ", uuid='" + uuid + '\'' +
                ", local=" + local +
                ", editable=" + editable +
                ", organisation=" + organisation +
                ", sharingGroupOrg=" + sharingGroupOrg +
                ", sharingGroupServer=" + sharingGroupServer +
                ", created='" + created + '\'' +
                ", modified='" + modified + '\'' +
                ", organisationUuid='" + organisationUuid + '\'' +
                ", orgId='" + orgId + '\'' +
                ", syncUserId='" + syncUserId + '\'' +
                ", roaming=" + roaming +
                ", syncOrgName='" + syncOrgName + '\'' +
                '}';
    }


}