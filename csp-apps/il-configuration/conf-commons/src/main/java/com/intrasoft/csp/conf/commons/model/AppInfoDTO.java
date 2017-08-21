package com.intrasoft.csp.conf.commons.model;


public class AppInfoDTO {

    private String name;
    private String recordDateTime;
    private ModulesInfoDTO modulesInfo;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(String recordDateTime) {
        this.recordDateTime = recordDateTime;
    }

    public ModulesInfoDTO getModulesInfo() {
        return modulesInfo;
    }

    public void setModuleInfo(ModulesInfoDTO moduleInfo) {
        this.modulesInfo = moduleInfo;
    }


    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", recordDateTime='" + recordDateTime + '\'' +
                ", modulesInfo=" + modulesInfo +
                '}';
    }
}
