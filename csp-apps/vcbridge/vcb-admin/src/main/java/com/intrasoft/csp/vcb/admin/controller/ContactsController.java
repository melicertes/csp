package com.intrasoft.csp.vcb.admin.controller;

import com.intrasoft.csp.vcb.admin.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactsController {

    @Autowired
    ContactsService contactsService;

    @RequestMapping(value = "/contacts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity contacts() {
        return new ResponseEntity<>(contactsService.fetchPersonContacts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/contact",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity contact(@RequestParam(value = "email", required = true) String email) {
        return new ResponseEntity<>(contactsService.fetchPersonContact(email), HttpStatus.OK);
    }
}
