package com.instrasoft.csp.ccs.domain.data;


public class ModuleRow {

    private String icon;
    private String shortName;
    private String fullName;
    private String version;
    private String released;
    private String isDefault;
    private Integer priority;
    private String hash;
    private String btn;


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }


    @Override
    public String toString() {
        return "ModuleRow{" +
                "icon='" + icon + '\'' +
                ", shortName='" + shortName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", version='" + version + '\'' +
                ", released='" + released + '\'' +
                ", isDefault='" + isDefault + '\'' +
                ", priority=" + priority +
                ", hash='" + hash + '\'' +
                ", btn='" + btn + '\'' +
                '}';
    }
}
