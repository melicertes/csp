package com.intrasoft.csp.misp.commons.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Map;



//@JsonIgnoreProperties(ignoreUnknown = true)

public class OrganisationDTO {


    private String id;

    private String name;

    private String alias;

    private boolean anonymise;

    private String dateCreated;

    private String dateModified;

    private String description;

    private String type;

    private String nationality;

    private String sector;

    private String createdBy;

    private String uuid;

    private String contacts;

    private boolean local;

    private String landingPage;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAnonymise() {
        return anonymise;
    }


    public void setAnonymise(boolean anonymise) {
        this.anonymise = anonymise;
    }


    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }


    public boolean isLocal() {
        return local;
    }


    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    @Override
    public String toString() {
        return "Organisation {" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", anonymise=" + anonymise +
                ", date_created='" + dateCreated + '\'' +
                ", date_modified='" + dateModified + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", nationality='" + nationality + '\'' +
                ", sector='" + sector + '\'' +
                ", created_by='" + createdBy + '\'' +
                ", uuid='" + uuid + '\'' +
                ", contacts='" + contacts + '\'' +
                ", local=" + local +
                ", landingPage='" + landingPage + '\'' +
                '}';
    }
}
