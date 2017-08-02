package com.intrasoft.csp.ccs.server.domain.data.form;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ManagementFormModule {

    @JsonProperty("moduleId")
    private Long moduleId;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("shortName")
    private String shortName;

    @JsonProperty("installedVersion")
    private String installedVersion;

    @JsonProperty("setVersion")
    private String setVersion;


    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getInstalledVersion() {
        return installedVersion;
    }

    public void setInstalledVersion(String installedVersion) {
        this.installedVersion = installedVersion;
    }

    public String getSetVersion() {
        return setVersion;
    }

    public void setSetVersion(String setVersion) {
        this.setVersion = setVersion;
    }


    @Override
    public String toString() {
        return "ManagementFormModule{" +
                "moduleId=" + moduleId +
                ", enabled=" + enabled +
                ", shortName='" + shortName + '\'' +
                ", installedVersion='" + installedVersion + '\'' +
                ", setVersion='" + setVersion + '\'' +
                '}';
    }
}
