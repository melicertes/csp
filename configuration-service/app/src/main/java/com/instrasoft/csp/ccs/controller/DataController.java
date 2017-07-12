package com.instrasoft.csp.ccs.controller;


import com.instrasoft.csp.ccs.config.DataContextUrl;
import com.instrasoft.csp.ccs.config.HttpStatusResponseType;
import com.instrasoft.csp.ccs.config.PagesContextUrl;
import com.instrasoft.csp.ccs.domain.api.Response;
import com.instrasoft.csp.ccs.domain.api.ResponseError;
import com.instrasoft.csp.ccs.domain.data.*;
import com.instrasoft.csp.ccs.domain.postgresql.*;
import com.instrasoft.csp.ccs.repository.*;
import com.instrasoft.csp.ccs.utils.FileHelper;
import com.instrasoft.csp.ccs.utils.JodaConverter;
import com.instrasoft.csp.ccs.utils.VersionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController implements DataContextUrl, PagesContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    @Value("${server.file.repository}")
    String fileRepository;
    @Value("${server.file.temp}")
    String fileTemp;
    @Value("${server.digest.algorithm}")
    String digestAlgorithm;

    @Autowired
    CspRepository cspRepository;

    @Autowired
    CspIpRepository cspIpRepository;

    @Autowired
    CspContactRepository cspContactRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    ModuleVersionRepository moduleVersionRepository;


    /*
    Dashboard and Manage
     */
    @RequestMapping(value = DATA_BASEURL + DATA_DASHBOARD,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity dashboard() {
        List<DashboardRow> rows = new ArrayList<>();
        List<Csp> csps = cspRepository.findAll();


        for (Csp csp : csps) {
            DashboardRow row = new DashboardRow();

            row.setIcon("<i class=\"fa fa-cog\"></i>");
            row.setName(csp.getName());
            row.setDomain(csp.getDomainName());
            row.setTs("<span class=\"text-success\">" + csp.getRegistrationDate() + "</span>");
            /**
             * @TODO
             */
            row.setStatus("<span class=\"text-success\"><i class=\"fa fa-info-circle\"></i> Up-to-date</span>");
            List<String> confUpdates = new ArrayList<>();
            List<String> reportUpdates = new ArrayList<>();
            row.setConfUpdates(confUpdates);
            row.setReportUpdates(reportUpdates);
            row.setBtn("<a class=\"btn btn-xs btn-default\" href=\""+PAGES_MANAGE+"?cspId=" + csp.getId() + "\"><i class=\"fa fa-wrench\"></i> Manage</a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    /*
    CSP
     */
    @RequestMapping(value = DATA_BASEURL + DATA_CSPS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity csps() {
        List<CspRow> rows = new ArrayList<>();
        List<Csp> csps = cspRepository.findAll();

        for (Csp csp : csps) {
            CspRow row = new CspRow();

            row.setIcon("<i class=\"fa fa-cog\"></i>");
            row.setCspId(csp.getId());
            row.setName(csp.getName());
            row.setDomainName(csp.getDomainName());
            row.setRegistrationDate(csp.getRegistrationDate());
            row.setInternalIps(cspIpRepository.findByCspIdAndExternal(csp.getId(), 0));
            row.setExternalIps(cspIpRepository.findByCspIdAndExternal(csp.getId(), 1));

            List<CspContact> cspContacts = cspContactRepository.findByCspId(csp.getId());
            List<String> contacts = new ArrayList<>();
            for (CspContact cspContact : cspContacts) {
                contacts.add(cspContact.toRow());
            }
            row.setContacts(contacts);

            row.setBtn("<a class=\"btn btn-xs btn-success\" href=\""+PAGES_CSP_UPDATE+"?cspId=" + csp.getId()+"\"><i class=\"fa fa-edit\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-default csp-delete\" data-csp-id=\""+csp.getId()+"\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }

    @RequestMapping(value = DATA_BASEURL + DATA_CSP_SAVE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerCsp(@RequestBody CspForm cspForm) {
        LOG.info(DATA_BASEURL + DATA_CSP_SAVE + ": POST received");

        /**
         * @TODO
         * Error checking. Send 200 HTTP response to be easily managed from js.
         * Shall name, domain_name be unique ???
         */
        //Check if contact name, email, type are of equal size
        if ((cspForm.getContactNames().size()+ cspForm.getContactEmails().size()+ cspForm.getContactTypes().size()) % 3 != 0) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_CSP_SAVE_INCONSISTENT_CONTACT.code(), HttpStatusResponseType.DATA_CSP_SAVE_INCONSISTENT_CONTACT.text(), HttpStatusResponseType.DATA_CSP_SAVE_INCONSISTENT_CONTACT.exception());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        //Check if csp_id exists
        if (cspRepository.findOne(cspForm.getCspId()) != null) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.code(), HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.text(), HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.exception());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }


        //save csp
        Csp csp = new Csp();
        csp.setId(cspForm.getCspId());
        csp = this.persistCsp(csp, cspForm);

        Response response = new Response(HttpStatusResponseType.DATA_CSP_SAVE_OK.code(), HttpStatusResponseType.DATA_CSP_SAVE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = DATA_BASEURL + DATA_CSP_UPDATE + "/{cspId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity updateCsp(@PathVariable String cspId, @RequestBody CspForm cspForm) {
        LOG.info(DATA_BASEURL + DATA_CSP_UPDATE + ": POST received");

        try {
            cspContactRepository.removeByCspId(cspId);
            cspIpRepository.removeByCspId(cspId);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_CSP_UPDATE_ERROR.code(), HttpStatusResponseType.DATA_CSP_UPDATE_ERROR.text(), e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        Csp csp = cspRepository.findOne(cspId);
        csp = this.persistCsp(csp, cspForm);



        Response response = new Response(HttpStatusResponseType.DATA_CSP_UPDATE_OK.code(), HttpStatusResponseType.DATA_CSP_UPDATE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = DATA_BASEURL + DATA_CSP_REMOVE + "/{cspId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeCsp(@PathVariable String cspId) {
        LOG.info(DATA_BASEURL + DATA_CSP_REMOVE  + "/" + cspId + ": POST received");

        try {
            cspContactRepository.removeByCspId(cspId);
            cspIpRepository.removeByCspId(cspId);
            cspRepository.delete(cspId);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_CSP_DELETE_ERROR.code(), HttpStatusResponseType.DATA_CSP_DELETE_ERROR.text(), e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        Response response = new Response(HttpStatusResponseType.DATA_CSP_DELETE_OK.code(), HttpStatusResponseType.DATA_CSP_DELETE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    Modules
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULES,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity modules() {
        List<ModuleRow> rows = new ArrayList<>();
        List<Module> modules = moduleRepository.findAll();

        for (Module module : modules) {
            ModuleRow row = new ModuleRow();

            row.setIcon("<i class=\"fa fa-circle-o-notch\"></i>");
            row.setShortName(module.getName());

            ModuleVersion moduleVersion = moduleVersionRepository.findByModuleId(module.getId());
            row.setFullName(moduleVersion.getFullName());
            row.setVersion(VersionParser.toString(moduleVersion.getVersion()));
            row.setReleased(moduleVersion.getReleasedOn());

            row.setIsDefault("<i class=\"fa fa-square-o\"></i>");
            if (module.getIsDefault() == 1) {
                row.setIsDefault("<i class=\"fa fa-square text-success\"></i>");
            }

            row.setPriority(module.getStartPriority());
            row.setHash(moduleVersion.getHash());
            row.setBtn("<a class=\"btn btn-xs btn-success\" href=\""+PAGES_MODULE_UPDATE+"?moduleId=" + module.getId()+"\"><i class=\"fa fa-edit\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-default\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }




    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_SAVE,
            headers=("content-type=multipart/*"),
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerModule(@RequestParam("module_short_name") String shortName,
                                  @RequestParam("module_full_name") String fullName,
                                  @RequestParam("module_version") String version,
                                  @RequestParam(value = "module_default", required = false, defaultValue = "") String isDefault,
                                  @RequestParam("module_priority") Integer priority,
                                  @RequestParam("module_file") MultipartFile uploadFile) {

        if (uploadFile.isEmpty()) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_EMPTY_FILE.code(), HttpStatusResponseType.DATA_MODULE_SAVE_EMPTY_FILE.text(), HttpStatusResponseType.DATA_MODULE_SAVE_EMPTY_FILE.exception());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        try {
            Module module = new Module();
            module.setName(shortName);
            module.setStartPriority(priority);
            module.setIsDefault(0);
            if (isDefault.equals("on")) {
                module.setIsDefault(1);
            }
            String hash = FileHelper.saveUploadedFile(fileTemp, fileRepository, uploadFile, digestAlgorithm);

            //save after all exceptions
            module = moduleRepository.save(module);
            LOG.info(module.getId().toString());
            ModuleVersion moduleVersion = new ModuleVersion();
            moduleVersion.setModuleId(module.getId());
            moduleVersion.setFullName(fullName);
            moduleVersion.setVersion(VersionParser.fromString(version));
            moduleVersion.setReleasedOn(JodaConverter.getCurrentJodaString());
            moduleVersion.setHash(hash);
            moduleVersionRepository.save(moduleVersion);

            /**
             * @TODO Check if {@link ModuleVersion exists}????
             */
            Response response = new Response(HttpStatusResponseType.DATA_MODULE_SAVE_OK.code(), HttpStatusResponseType.DATA_MODULE_SAVE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException || e instanceof IOException || e instanceof NoSuchFileException) {
                ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_HASH_FILE.code(), HttpStatusResponseType.DATA_MODULE_SAVE_HASH_FILE.text(), HttpStatusResponseType.DATA_MODULE_SAVE_HASH_FILE.exception());
                return new ResponseEntity<>(error, HttpStatus.OK);
            }
            else {
                ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_UNIQUE.code(), HttpStatusResponseType.DATA_MODULE_SAVE_UNIQUE.text(), HttpStatusResponseType.DATA_MODULE_SAVE_UNIQUE.exception());
                return new ResponseEntity<>(error, HttpStatus.OK);
            }
        }
    }




    private Csp persistCsp(Csp csp, CspForm cspForm) {
        csp.setName(cspForm.getName());
        csp.setDomainName(cspForm.getDomainName());
        csp.setRegistrationDate(JodaConverter.getCurrentJodaString());
        csp = cspRepository.save(csp);

        //save csp contacts
        cspContactRepository.removeByCspId(csp.getId());
        for (Contact contact : cspForm.getContacts()) {
            CspContact cspContact = new CspContact();
            cspContact.setCspId(csp.getId());
            cspContact.setPersonName(contact.getPersonName());
            cspContact.setPersonEmail(contact.getPersonEmail());
            cspContact.setContactType(contact.getContactType());
            cspContactRepository.save(cspContact);
        }

        //save IPs
        cspIpRepository.removeByCspId(csp.getId());
        for (String ip : cspForm.getInternalIps()) {
            CspIp cspIp = new CspIp();
            cspIp.setCspId(csp.getId());
            cspIp.setIp(ip);
            cspIp.setExternal(0);
            cspIpRepository.save(cspIp);
        }
        for (String ip : cspForm.getExternalIps()) {
            CspIp cspIp = new CspIp();
            cspIp.setCspId(csp.getId());
            cspIp.setIp(ip);
            cspIp.setExternal(1);
            cspIpRepository.save(cspIp);
        }

        return csp;
    }
}
