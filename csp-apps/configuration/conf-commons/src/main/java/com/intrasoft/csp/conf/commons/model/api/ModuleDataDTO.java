package com.intrasoft.csp.conf.commons.model.api;



public class ModuleDataDTO {

    private String fullName;
    private Integer version;
    private String installedOn;
    private Boolean active;
    private String hash;
    private Float startPriority;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(String installedOn) {
        this.installedOn = installedOn;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Float getStartPriority() {
        return startPriority;
    }

    public void setStartPriority(Float startPriority) {
        this.startPriority = startPriority;
    }


    @Override
    public String toString() {
        return "ModuleData{" +
                "fullName='" + fullName + '\'' +
                ", version=" + version +
                ", installedOn='" + installedOn + '\'' +
                ", active=" + active +
                ", hash='" + hash + '\'' +
                ", startPriority=" + startPriority +
                '}';
    }
}
