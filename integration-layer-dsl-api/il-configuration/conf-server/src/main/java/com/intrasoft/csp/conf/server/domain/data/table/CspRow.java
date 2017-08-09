package com.intrasoft.csp.conf.server.domain.data.table;


import java.util.List;

public class CspRow {

    private String icon;
    private String cspId;
    private String name;
    private String domainName;
    private String registrationDate;
    private List<String> internalIps;
    private List<String> externalIps;
    private List<String> contacts;
    private String btn;


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

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

    public List<String> getInternalIps() {
        return internalIps;
    }

    public void setInternalIps(List<String> internalIps) {
        this.internalIps = internalIps;
    }

    public List<String> getExternalIps() {
        return externalIps;
    }

    public void setExternalIps(List<String> externalIps) {
        this.externalIps = externalIps;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }


    @Override
    public String toString() {
        return "CspRow{" +
                "icon='" + icon + '\'' +
                ", cspId='" + cspId + '\'' +
                ", name='" + name + '\'' +
                ", domainName='" + domainName + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", internalIps=" + internalIps +
                ", externalIps=" + externalIps +
                ", contacts=" + contacts +
                ", btn='" + btn + '\'' +
                '}';
    }
}
