package com.instrasoft.csp.ccs.domain.api;


public class ModuleInfo {

    private String name;
    private ModuleData additionalProperties;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleData getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(ModuleData additionalProperties) {
        this.additionalProperties = additionalProperties;
    }


    @Override
    public String toString() {
        return "ModuleInfo{" +
                "name='" + name + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
