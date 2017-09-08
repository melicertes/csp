package com.intrasoft.csp.conf.commons.model;


public class ModuleUpdateInfoDTO {

    private String name;
    private String description;
    private String version;
    private String released;
    private String hash;
    private Integer startPriority;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getStartPriority() {
        return startPriority;
    }

    public void setStartPriority(Integer startPriority) {
        this.startPriority = startPriority;
    }

    @Override
    public String toString() {
        return "ModuleUpdateInfoDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", released='" + released + '\'' +
                ", hash='" + hash + '\'' +
                ", startPriority=" + startPriority +
                '}';
    }
}
