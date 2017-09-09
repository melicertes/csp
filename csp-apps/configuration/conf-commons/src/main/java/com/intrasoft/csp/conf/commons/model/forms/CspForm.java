package com.intrasoft.csp.conf.commons.model.forms;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrasoft.csp.conf.commons.model.ContactDTO;
import com.intrasoft.csp.conf.commons.model.api.ContactDetailsDTO;
import com.intrasoft.csp.conf.commons.types.ContactType;

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


    public List<ContactDTO> getContacts() {
        List<ContactDTO> contacts = new ArrayList<>();
        for(int i=0; i<contactNames.size(); i++) {
            ContactDTO contact = new ContactDTO();
            contact.setPersonName(contactNames.get(i));
            contact.setPersonEmail(contactEmails.get(i));
            contact.setContactType(contactTypes.get(i));
            contacts.add(contact);
        }
        return contacts;
    }

    public List<ContactDetailsDTO> getContactDetails() {
        List<ContactDetailsDTO> contacts = new ArrayList<>();
        for(int i=0; i<contactNames.size(); i++) {
            ContactDetailsDTO contact = new ContactDetailsDTO();
            contact.setPersonName(contactNames.get(i));
            contact.setPersonEmail(contactEmails.get(i));
            contact.setContactType(ContactType.fromValue(contactTypes.get(i)));
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
