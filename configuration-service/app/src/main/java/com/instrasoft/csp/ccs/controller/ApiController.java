package com.instrasoft.csp.ccs.controller;

import com.instrasoft.csp.ccs.config.ApiContextUrl;
import com.instrasoft.csp.ccs.config.HttpStatusResponseType;
import com.instrasoft.csp.ccs.domain.api.*;
import com.instrasoft.csp.ccs.domain.postgresql.Csp;
import com.instrasoft.csp.ccs.domain.postgresql.CspContact;
import com.instrasoft.csp.ccs.domain.postgresql.CspIp;
import com.instrasoft.csp.ccs.repository.*;
import com.instrasoft.csp.ccs.utils.JodaConverter;
import com.instrasoft.csp.ccs.utils.JsonPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ApiController implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    CspRepository cspRepository;

    @Autowired
    CspIpRepository cspIpRepository;

    @Autowired
    CspContactRepository cspContactRepository;

    @Autowired
    CspInfoRepository cspInfoRepository;



    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_UPDATES + "/{cspId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updates(@PathVariable String cspId) {

        LOG.info("/v" + API_V1 + API_UPDATES + "/" + cspId + ": GET received");

//        if (!cspRepository.exists(cspId)) {
//          ResponseError error = new ResponseError(1020, "Transaction failure 1", "cspId not found; failure to identify cspId presented");
//            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//        }
        try {
            //Integer x = 1/0;
            Csp csp = cspRepository.findOne(cspId);
            if (csp.equals(null)) throw new EntityNotFoundException();
            //System.out.println(cspRepository.findOne(cspId).getRegistrationDateFormatted());
        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) {
                ResponseError error = new ResponseError(1020, "Transaction failure 1", "cspId not found; failure to identify cspId presented");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            ResponseError error = new ResponseError(1020, "Transaction failure 2", e.toString());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }


        /*
        Example
         */
        UpdateInformation x = new UpdateInformation();
        x.setDateChanged("2017-06-25T20:30:54.844Z");

        ModuleUpdateInfo y1 = new ModuleUpdateInfo();
        y1.setName("configuration-service-client:1.0.1");
        y1.setDescription("Rolling update of 20170331 to fix CSR-3988");
        y1.setVersion(101290);
        y1.setReleased("2017-03-30T11:12:33Z");
        //y1.setIncremental(true);
        ModuleUpdateInfo y2 = new ModuleUpdateInfo();
        y2.setName("configuration-service-client:1.0.2");
        y2.setDescription("Rolling update of 20170403 to fix CSR-3989");
        y2.setVersion(10200);
        y2.setReleased("2017-04-03T12:12:33Z");
        //y2.setIncremental(true);
        List<ModuleUpdateInfo> list = new ArrayList<>();
        list.add(y1);
        list.add(y2);

        HashMap<String, List<ModuleUpdateInfo>> available = new HashMap<>();
        available.put("configuration-service-client", list);
        x.setAvailable(available);

        return new ResponseEntity<>(x, HttpStatus.OK);
    }


    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_REGISTER + "/{cspId}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity register(@PathVariable String cspId, @RequestBody Registration cspRegistration) {
        LOG.info("/v" + API_V1 + API_UPDATES + "/" + cspId + ": POST received");


        System.out.println(cspId);
        System.out.println(cspRegistration.toString());
        System.out.println(JsonPrinter.toJsonPrettyString(cspRegistration));

        try {
            if (cspRepository.exists(cspId) && cspRegistration.getRegistrationIsUpdate()) {
                // update Csp basic info
                Csp csp = this.getCspFromRegistration(cspRegistration);
                csp.setId(cspId);
                cspRepository.save(csp);
            }
            else if (!cspRepository.exists(cspId) && !cspRegistration.getRegistrationIsUpdate()) {
                // insert Csp basic info
                Csp csp = this.getCspFromRegistration(cspRegistration);
                cspRepository.save(csp);
            }
            else if (cspRepository.exists(cspId) && !cspRegistration.getRegistrationIsUpdate()) {
                ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_400.code(), HttpStatusResponseType.API_REGISTER_400.text(), "");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            else {
                ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_404.code(), HttpStatusResponseType.API_REGISTER_404.text(), "");
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }

            //IPs (external and internal)
            cspIpRepository.removeByCspId(cspId);
            this.updateCspIpsFromRegistration(cspId, cspRegistration, 1);
            this.updateCspIpsFromRegistration(cspId, cspRegistration, 0);

            //Contacts
            cspContactRepository.removeByCspId(cspId);
            this.updateCspContactsFromRegistration(cspId, cspRegistration);

            //ModuleInfo
            //cspInfoRepository.removeByCspId(cspId);



            /**
             * @TODO ModuleInfo
             */


        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_500.code(), HttpStatusResponseType.API_REGISTER_500.text(), e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }


        Response response = new Response(HttpStatusResponseType.API_REGISTER_200.code(), HttpStatusResponseType.API_REGISTER_200.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_UPDATE + "/{cspId}" + "/{updateHash}",
            method = RequestMethod.GET)
    public ResponseEntity update(@PathVariable String cspId, @PathVariable String updateHash) {

        ClassPathResource pdfFile = new ClassPathResource("test1.pdf");

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(pdfFile.contentLength())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new InputStreamResource(pdfFile.getInputStream()));
        } catch (IOException e) {
            //e.printStackTrace();
            ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_500.code(), HttpStatusResponseType.API_REGISTER_500.text(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).contentType(MediaType.APPLICATION_JSON).body(error);
        }
    }


    private Csp getCspFromRegistration(Registration cspRegistration) throws ParseException {
        Csp csp = new Csp();
        csp.setName(cspRegistration.getName());
        csp.setDomainName(cspRegistration.getDomainName());
        csp.setRegistrationDate(cspRegistration.getRegistrationDate());
        return csp;
    }

    private void updateCspIpsFromRegistration(String cspId, Registration cspRegistration, Integer external) {
        List<String> ips;
        if (external == 1) {
            ips = cspRegistration.getExternalIPs();
        } else {
            ips = cspRegistration.getInternalIPs();
        }

        for (String ip : ips) {
            CspIp cspIp = new CspIp();
            cspIp.setCspId(cspId);
            cspIp.setIp(ip);
            cspIp.setExternal(external);
            cspIpRepository.save(cspIp);
        }
    }

    private void updateCspContactsFromRegistration(String cspId, Registration cspRegistration) {
        List<ContactDetails> contacts = cspRegistration.getContacts();

        for (ContactDetails contact : contacts) {
            CspContact cspContact = new CspContact();
            cspContact.setCspId(cspId);
            cspContact.setPersonName(contact.getPersonName());
            cspContact.setPersonEmail(contact.getPersonEmail());
            cspContact.setContactType(contact.getContactType());
            cspContactRepository.save(cspContact);
        }
    }
}
