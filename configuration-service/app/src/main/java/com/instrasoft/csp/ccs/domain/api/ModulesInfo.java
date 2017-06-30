package com.instrasoft.csp.ccs.domain.api;


import java.util.List;

public class ModulesInfo {

    private List<ModuleInfo> modules;

    public List<ModuleInfo> getModules() {
        return modules;
    }

    public void setModules(List<ModuleInfo> modules) {
        this.modules = modules;
    }


    @Override
    public String toString() {
        return "ModulesInfo{" +
                "modules=" + modules +
                '}';
    }
}
