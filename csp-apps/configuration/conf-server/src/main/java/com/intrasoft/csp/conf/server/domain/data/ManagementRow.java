package com.intrasoft.csp.conf.server.domain.data;


import java.util.List;

public class ManagementRow {

    private Long moduleId;
    private Integer isModuleDefault;
    private Boolean isModuleEnabled;
    private String moduleShortName;
    private Integer installedVersion;
    private List<Integer> availableVersions;
    private List<String> availableVersionsT;
    private Integer selectedVersion;


    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getIsModuleDefault() {
        return isModuleDefault;
    }

    public void setIsModuleDefault(Integer isModuleDefault) {
        this.isModuleDefault = isModuleDefault;
    }

    public Boolean getModuleEnabled() {
        return isModuleEnabled;
    }

    public void setModuleEnabled(Boolean moduleEnabled) {
        isModuleEnabled = moduleEnabled;
    }

    public String getModuleShortName() {
        return moduleShortName;
    }

    public void setModuleShortName(String moduleShortName) {
        this.moduleShortName = moduleShortName;
    }

    public Integer getInstalledVersion() {
        return installedVersion;
    }

    public void setInstalledVersion(Integer installedVersion) {
        this.installedVersion = installedVersion;
    }

    public List<Integer> getAvailableVersions() {
        return availableVersions;
    }

    public void setAvailableVersions(List<Integer> availableVersions) {
        this.availableVersions = availableVersions;
    }

    public List<String> getAvailableVersionsT() {
        return availableVersionsT;
    }

    public void setAvailableVersionsT(List<String> availableVersionsT) {
        this.availableVersionsT = availableVersionsT;
    }

    public Integer getSelectedVersion() {
        return selectedVersion;
    }

    public void setSelectedVersion(Integer selectedVersion) {
        this.selectedVersion = selectedVersion;
    }


    @Override
    public String toString() {
        return "ManagementRow{" +
                "moduleId=" + moduleId +
                ", isModuleDefault=" + isModuleDefault +
                ", isModuleEnabled=" + isModuleEnabled +
                ", moduleShortName='" + moduleShortName + '\'' +
                ", installedVersion=" + installedVersion +
                ", availableVersions=" + availableVersions +
                ", availableVersionsT=" + availableVersionsT +
                ", selectedVersion=" + selectedVersion +
                '}';
    }
}
