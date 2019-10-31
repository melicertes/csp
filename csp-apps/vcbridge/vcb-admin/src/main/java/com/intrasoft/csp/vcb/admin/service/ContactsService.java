package com.intrasoft.csp.vcb.admin.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.client.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactsService {

    @Autowired
    TrustCirclesClient trustCirclesClient;


    public List<String> fetchPersonContacts() {
        List<String> emails = new ArrayList<>();

        List<PersonContact> personContacts = trustCirclesClient.getPersonContacts();

        for (PersonContact personContact : personContacts) {
            emails.add(personContact.getEmail());
        }

        return emails;
    }

    public PersonContact fetchPersonContact(String email) {
        PersonContact personContact = trustCirclesClient.getPersonContactByEmail(email);
        return personContact;
    }

}
