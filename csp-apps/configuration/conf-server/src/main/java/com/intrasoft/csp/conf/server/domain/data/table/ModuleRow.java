package com.intrasoft.csp.conf.server.domain.data.table;


public class ModuleRow {

    private String icon;
    private String shortName;
    private Integer startPriority;
    private String isDefault;
    private Long versionsCount;
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

    public Integer getStartPriority() {
        return startPriority;
    }

    public void setStartPriority(Integer priority) {
        this.startPriority = priority;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Long getVersionsCount() {
        return versionsCount;
    }

    public void setVersionsCount(Long versionsCount) {
        this.versionsCount = versionsCount;
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
                ", startPriority=" + startPriority +
                ", isDefault='" + isDefault + '\'' +
                ", versionsCount=" + versionsCount +
                ", btn='" + btn + '\'' +
                '}';
    }
}
