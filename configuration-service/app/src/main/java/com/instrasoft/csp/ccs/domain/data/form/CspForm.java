package com.instrasoft.csp.ccs.domain.data.form;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.instrasoft.csp.ccs.domain.data.Contact;

import java.util.ArrayList;
import java.util.List;

public class CspForm {

    @JsonProperty("csp_id")
    private String cspId;

    @JsonProperty("csp_name")
    private String name;

    @JsonProperty("csp_domain_name")
    private String domainName;

    @JsonProperty("csp_contact_name")
    private List<String> contactNames;

    @JsonProperty("csp_contact_email")
    private List<String> contactEmails;

    @JsonProperty("csp_contact_type")
    private List<String> contactTypes;

    @JsonProperty("csp_internal_ip")
    private List<String> internalIps;

    @JsonProperty("csp_external_ip")
    private List<String> externalIps;


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

    public List<String> getContactNames() {
        return contactNames;
    }

    public void setContactNames(List<String> contactNames) {
        this.contactNames = contactNames;
    }

    public List<String> getContactEmails() {
        return contactEmails;
    }

    public void setContactEmails(List<String> contactEmails) {
        this.contactEmails = contactEmails;
    }

    public List<String> getContactTypes() {
        return contactTypes;
    }

    public void setContactTypes(List<String> contactTypes) {
        this.contactTypes = contactTypes;
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


    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        for(int i=0; i<contactNames.size(); i++) {
            Contact contact = new Contact();
            contact.setPersonName(contactNames.get(i));
            contact.setPersonEmail(contactEmails.get(i));
            contact.setContactType(contactTypes.get(i));
            contacts.add(contact);
        }
        return contacts;
    }

    @Override
    public String toString() {
        return "CspRegistration{" +
                "cspId='" + cspId + '\'' +
                ", name='" + name + '\'' +
                ", domainName='" + domainName + '\'' +
                ", contactNames=" + contactNames +
                ", contactEmails=" + contactEmails +
                ", contactTypes=" + contactTypes +
                ", internalIps=" + internalIps +
                ", externalIps=" + externalIps +
                '}';
    }
}
