package com.intrasoft.csp.vcb.admin.service;


import com.intrasoft.csp.vcb.admin.model.dto.PersonContactDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContactsService {

    @Value("${tc.protocol}")
    private String protocol;
    @Value("${tc.host}")
    private String host;
    @Value("${tc.port}")
    private Integer port;
    @Value("${tc.path}")
    private String path;
    @Value("${tc.path.personcontacts}")
    private String pathPersonContacts;

    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    public ContactsService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<String> fetchPersonContacts() {
        List<String> emails = new ArrayList<>();

        PersonContactDTO[] personContacts = restTemplate.getForObject(tcPersonContactsEndpoint(), PersonContactDTO[].class);
        for (PersonContactDTO dto : personContacts) {
            emails.add(dto.getEmail());
        }

        return emails;
    }

    public PersonContactDTO fetchPersonContact(String email) {
        String url = this.tcPersonContactsEndpoint() + "?email=" + email;
        PersonContactDTO[] personContacts = restTemplate.getForObject(url, PersonContactDTO[].class);
        return personContacts[0];
    }


    private String tcPersonContactsEndpoint() {
        return this.protocol + "://" + this.host + ":" + this.port + this.path + this.pathPersonContacts;
    }

}
