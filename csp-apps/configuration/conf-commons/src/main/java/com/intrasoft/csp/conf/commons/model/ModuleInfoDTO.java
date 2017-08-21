package com.intrasoft.csp.conf.commons.model;


public class ModuleInfoDTO {

    private String name;
    private ModuleDataDTO additionalProperties;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleDataDTO getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(ModuleDataDTO additionalProperties) {
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
