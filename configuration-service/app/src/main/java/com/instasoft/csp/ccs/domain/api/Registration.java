package com.instasoft.csp.ccs.domain.api;


import java.util.List;

public class Registration {

    private String name;
    private String domainName;
    private String registrationDate;
    private List<String> externalIPs;
    private List<String> internalIPs;
    private Boolean registrationIsUpdate;
    private List<ContactDetails> contacts;
    private ModulesInfo moduleInfo;



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

    public List<ContactDetails> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactDetails> contacts) {
        this.contacts = contacts;
    }

    public ModulesInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModulesInfo modulesInfo) {
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
