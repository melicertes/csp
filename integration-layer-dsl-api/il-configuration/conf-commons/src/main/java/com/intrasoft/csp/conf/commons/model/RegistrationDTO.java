package com.intrasoft.csp.conf.commons.model;


import java.util.List;

public class RegistrationDTO {

    private String name;
    private String domainName;
    private String registrationDate;
    private List<String> externalIPs;
    private List<String> internalIPs;
    private Boolean registrationIsUpdate;
    private List<ContactDetailsDTO> contacts;
    private ModulesInfoDTO moduleInfo;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public List<String> getExternalIPs() {
        return externalIPs;
    }

    public void setExternalIPs(List<String> externalIPs) {
        this.externalIPs = externalIPs;
    }

    public List<String> getInternalIPs() {
        return internalIPs;
    }

    public void setInternalIPs(List<String> internalIPs) {
        this.internalIPs = internalIPs;
    }

    public Boolean getRegistrationIsUpdate() {
        return registrationIsUpdate;
    }

    public void setRegistrationIsUpdate(Boolean registrationIsUpdate) {
        this.registrationIsUpdate = registrationIsUpdate;
    }

    public List<ContactDetailsDTO> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDetailsDTO> contacts) {
        this.contacts = contacts;
    }

    public ModulesInfoDTO getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModulesInfoDTO modulesInfo) {
        this.moduleInfo = modulesInfo;
    }


    @Override
    public String toString() {
        return "Registration{" +
                "name='" + name + '\'' +
                ", domainName='" + domainName + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", externalIPs=" + externalIPs +
                ", internalIPs=" + internalIPs +
                ", registrationIsUpdate=" + registrationIsUpdate +
                ", contacts=" + contacts +
                ", modulesInfo=" + moduleInfo +
                '}';
    }
}
