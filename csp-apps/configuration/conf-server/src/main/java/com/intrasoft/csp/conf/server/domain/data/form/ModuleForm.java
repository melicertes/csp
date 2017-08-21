package com.intrasoft.csp.conf.server.domain.data.form;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ModuleForm {

    @JsonProperty("module_id")
    private Long moduleId;

    @JsonProperty("module_short_name")
    private String shortName;

    @JsonProperty("module_start_priority")
    private Integer startPriority;

    @JsonProperty(value = "module_is_default", defaultValue = "0", required = false)
    private Boolean isDefault;


    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
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

    public void setStartPriority(Integer startPriority) {
        this.startPriority = startPriority;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = false;
        if (isDefault.equals("on")) {
            this.isDefault = true;
        }
    }
    public void setIsDefault() {
        this.isDefault = false;
    }


    @Override
    public String toString() {
        return "ModuleForm{" +
                "moduleId=" + moduleId +
                ", shortName='" + shortName + '\'' +
                ", startPriority=" + startPriority +
                ", isDefault='" + isDefault + '\'' +
                '}';
    }
}
