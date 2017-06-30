package com.instrasoft.csp.ccs.domain.api;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateInformation {

    private String dateChanged;
    private HashMap<String, List<ModuleUpdateInfo>> available;


    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    public HashMap<String, List<ModuleUpdateInfo>> getAvailable() {
        return available;
    }

    public void setAvailable(HashMap<String, List<ModuleUpdateInfo>> available) {
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
