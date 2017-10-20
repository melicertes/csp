package com.intrasoft.csp.conf.commons.model.api;


import com.intrasoft.csp.conf.commons.types.ContactType;

public class ContactDetailsDTO {

    private String personName;
    private String personEmail;
    private ContactType contactType;


    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    @Override
    public String toString() {
        return "ContactDetails{" +
                "personName='" + personName + '\'' +
                ", personEmail='" + personEmail + '\'' +
                ", contactType=" + contactType +
                '}';
    }
}
