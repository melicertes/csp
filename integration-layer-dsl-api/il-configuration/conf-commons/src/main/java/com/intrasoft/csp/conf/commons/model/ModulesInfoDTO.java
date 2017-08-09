package com.intrasoft.csp.conf.commons.model;


import java.util.List;

public class ModulesInfoDTO {

    private List<ModuleInfoDTO> modules;

    public List<ModuleInfoDTO> getModules() {
        return modules;
    }

    public void setModules(List<ModuleInfoDTO> modules) {
        this.modules = modules;
    }


    @Override
    public String toString() {
        return "ModulesInfo{" +
                "modules=" + modules +
                '}';
    }
}
