package com.intrasoft.csp.ccs.server.domain.data.table;


public class ModuleVersionRow {

    private String icon;
    private String fullName;
    private String version;
    private String releasedOn;
    private String hash;
    private String description;
    private String btn;


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleasedOn() {
        return releasedOn;
    }

    public void setReleasedOn(String releasedOn) {
        this.releasedOn = releasedOn;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }


    @Override
    public String toString() {
        return "ModuleVersionRow{" +
                "icon='" + icon + '\'' +
                ", fullName='" + fullName + '\'' +
                ", version=" + version +
                ", releasedOn='" + releasedOn + '\'' +
                ", hash='" + hash + '\'' +
                ", description='" + description + '\'' +
                ", btn='" + btn + '\'' +
                '}';
    }
}
