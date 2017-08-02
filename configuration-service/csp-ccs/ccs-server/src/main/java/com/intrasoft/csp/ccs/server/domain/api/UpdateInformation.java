package com.intrasoft.csp.ccs.server.domain.api;


import java.util.LinkedHashMap;
import java.util.List;

public class UpdateInformation {

    private String dateChanged;
    private LinkedHashMap<String, List<ModuleUpdateInfo>> available;


    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    public LinkedHashMap<String, List<ModuleUpdateInfo>> getAvailable() {
        return available;
    }

    public void setAvailable(LinkedHashMap<String, List<ModuleUpdateInfo>> available) {
        this.available = available;
    }


    @Override
    public String toString() {
        return "UpdateInformation{" +
                "dateChanged='" + dateChanged + '\'' +
                ", available=" + available +
                '}';
    }
}
