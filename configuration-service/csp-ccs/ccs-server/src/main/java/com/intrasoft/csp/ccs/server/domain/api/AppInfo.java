package com.intrasoft.csp.ccs.server.domain.api;


public class AppInfo {

    private String name;
    private String recordDateTime;
    private ModulesInfo modulesInfo;


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

    public ModulesInfo getModulesInfo() {
        return modulesInfo;
    }

    public void setModuleInfo(ModulesInfo moduleInfo) {
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
