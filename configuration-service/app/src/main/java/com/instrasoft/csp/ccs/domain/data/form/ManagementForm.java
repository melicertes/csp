package com.instrasoft.csp.ccs.domain.data.form;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ManagementForm {

    @JsonProperty("cspId")
    private String cspId;

    @JsonProperty("modules")
    private List<ManagementFormModule> modules;


    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public List<ManagementFormModule> getModules() {
        return modules;
    }

    public void setModules(List<ManagementFormModule> modules) {
        this.modules = modules;
    }


    @Override
    public String toString() {
        return "ManagementForm{" +
                "cspId='" + cspId + '\'' +
                ", modules=" + modules +
                '}';
    }
}
