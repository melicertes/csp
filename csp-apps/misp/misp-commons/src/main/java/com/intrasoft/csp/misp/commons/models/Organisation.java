package com.intrasoft.csp.misp.commons.models;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "alias",
        "anonymise",
        "date_created",
        "date_modified",
        "description",
        "type",
        "nationality",
        "sector",
        "created_by",
        "uuid",
        "contacts",
        "local",
        "landingpage"
})

@JsonRootName(value = "Organisation")
public class Organisation implements Serializable {

/*
    How do i generate this?
    private static final long serialVersionUID

    Remember, Joda for dates?
    Remember, build a toString()
*/


    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("alias")
    private String alias;
    @JsonProperty("anonymise")
    private boolean anonymise;
    @JsonProperty("date_created")
    private String dateCreated;
    @JsonProperty("date_modified")
    private String dateModified;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;
    @JsonProperty("nationality")
    private String nationality;
    @JsonProperty("sector")
    private String sector;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("contacts")
    private String contacts;
    @JsonProperty("local")
    private boolean local;
    @JsonProperty("landingpage")
    private String landingPage;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("id")
    public String getId() {
        return id;
    }
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }
    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }
    @JsonProperty("anonymise")
    public boolean isAnonymise() {
        return anonymise;
    }

    @JsonProperty("anonymise")
    public void setAnonymise(boolean anonymise) {
        this.anonymise = anonymise;
    }

    @JsonProperty("date_created")
    public String getDateCreated() {
        return dateCreated;
    }
    @JsonProperty("date_created")
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    @JsonProperty("date_modified")
    public String getDateModified() {
        return dateModified;
    }
    @JsonProperty("date_modified")
    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }
    @JsonProperty("type")
    public String getType() {
        return type;
    }
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }
    @JsonProperty("nationality")
    public String getNationality() {
        return nationality;
    }
    @JsonProperty("nationality")
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    @JsonProperty("sector")
    public String getSector() {
        return sector;
    }
    @JsonProperty("sector")
    public void setSector(String sector) {
        this.sector = sector;
    }
    @JsonProperty("created_by")
    public String getCreatedBy() {
        return createdBy;
    }
    @JsonProperty("created_by")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }
    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("contacts")
    public String getContacts() {
        return contacts;
    }
    @JsonProperty("contacts")
    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    @JsonProperty("local")
    public boolean isLocal() {
        return local;
    }

    @JsonProperty("local")
    public void setLocal(boolean local) {
        this.local = local;
    }
    @JsonProperty("landingpage")
    public String getLandingPage() {
        return landingPage;
    }
    @JsonProperty("landingpage")
    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
