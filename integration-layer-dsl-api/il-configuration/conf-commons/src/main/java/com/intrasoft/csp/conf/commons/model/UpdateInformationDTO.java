package com.intrasoft.csp.conf.commons.model;


import java.util.LinkedHashMap;
import java.util.List;

public class UpdateInformationDTO {

    private String dateChanged;
    private LinkedHashMap<String, List<ModuleUpdateInfoDTO>> available;


    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    public LinkedHashMap<String, List<ModuleUpdateInfoDTO>> getAvailable() {
        return available;
    }

    public void setAvailable(LinkedHashMap<String, List<ModuleUpdateInfoDTO>> available) {
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
