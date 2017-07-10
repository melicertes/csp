package com.instrasoft.csp.ccs.domain.api;


import com.instrasoft.csp.ccs.config.ContactType;

public class ContactDetails {

    private String personName;
    private String personEmail;
    private String contactType;


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

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
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
