package com.instrasoft.csp.ccs.domain.api;


public class AppInfo {

    private String name;
    private String recordDateTime;
    private ModuleInfo moduleInfo;


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

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }


    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", recordDateTime='" + recordDateTime + '\'' +
                ", moduleInfo=" + moduleInfo +
                '}';
    }
}
