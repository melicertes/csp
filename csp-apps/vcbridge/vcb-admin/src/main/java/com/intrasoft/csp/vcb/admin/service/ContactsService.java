package com.intrasoft.csp.vcb.admin.service;


import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.model.PersonContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
