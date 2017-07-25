package com.instrasoft.csp.ccs.controller;


import com.instrasoft.csp.ccs.config.context.DataContextUrl;
import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;
import com.instrasoft.csp.ccs.config.context.PagesContextUrl;
import com.instrasoft.csp.ccs.domain.api.Response;
import com.instrasoft.csp.ccs.domain.api.ResponseError;
import com.instrasoft.csp.ccs.domain.data.Contact;
import com.instrasoft.csp.ccs.domain.data.form.CspForm;
import com.instrasoft.csp.ccs.domain.data.form.ManagementForm;
import com.instrasoft.csp.ccs.domain.data.form.ManagementFormModule;
import com.instrasoft.csp.ccs.domain.data.form.ModuleForm;
import com.instrasoft.csp.ccs.domain.data.table.CspRow;
import com.instrasoft.csp.ccs.domain.data.table.DashboardRow;
import com.instrasoft.csp.ccs.domain.data.table.ModuleRow;
import com.instrasoft.csp.ccs.domain.data.table.ModuleVersionRow;
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
import java.nio.file.NoSuchFileException;
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
    CspInfoRepository cspInfoRepository;

    @Autowired
    CspModuleInfoRepository cspModuleInfoRepository;

    @Autowired
    CspManagementRepository cspManagementRepository;

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
        LOG.info(DATA_BASEURL + DATA_DASHBOARD + ": GET received");

        List<DashboardRow> rows = new ArrayList<>();
        List<Csp> csps = cspRepository.findAll();


        for (Csp csp : csps) {
            CspInfo cspInfo = cspInfoRepository.findByCspId(csp.getId()).get(0);

            DashboardRow row = new DashboardRow();

            row.setIcon("<i class=\"fa fa-cog\"></i>");
            row.setName(csp.getName());
            row.setDomain(csp.getDomainName());
            row.setRegistrationDate(csp.getRegistrationDate());
            row.setLastUpdate(cspInfo.getRecordDateTime());

            List<String> confUpdates = new ArrayList<>();
            List<CspManagement> cspManagements = cspManagementRepository.findByCspId(csp.getId());
            for(CspManagement cspManagement : cspManagements) {
                confUpdates.add(moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getFullName());
            }
            row.setConfUpdates(confUpdates);

            List<String> reportUpdates = new ArrayList<>();
            List<CspModuleInfo> cspModuleInfos = cspModuleInfoRepository.findByCspInfoId(cspInfo.getId());
            for(CspModuleInfo cspModuleInfo : cspModuleInfos) {
                reportUpdates.add(moduleVersionRepository.findOne(cspModuleInfo.getModuleVersionId()).getFullName());
            }
            row.setReportUpdates(reportUpdates);

            row.setBtn("<a class=\"btn btn-xs btn-default\" title=\"Manage CSP: " + csp.getName() + " \" href=\""+PAGES_MANAGE+"?cspId=" + csp.getId() + "\"><i class=\"fa fa-wrench\"></i></a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    @RequestMapping(value = DATA_BASEURL + DATA_MANAGE,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity manage(@RequestBody ManagementForm managementForm) {
        LOG.info(DATA_BASEURL + DATA_MANAGE + ": POST received");

        String cspId = managementForm.getCspId();
        List<ManagementFormModule> managementFormModules = managementForm.getModules();
        for (ManagementFormModule formModule : managementFormModules) {
            if (formModule.getEnabled()) {
                if (cspManagementRepository.findByCspIdAndModuleId(cspId, formModule.getModuleId()).size() != 0) {
                    //remove old versions
                    cspManagementRepository.removeByCspIdAndModuleId(cspId, formModule.getModuleId());
                }
                //insert new row
                CspManagement cspManagement = new CspManagement();
                cspManagement.setCspId(cspId);
                cspManagement.setModuleId(formModule.getModuleId());
                ModuleVersion moduleVersion = moduleVersionRepository.findByModuleIdAndVersion(formModule.getModuleId(), Integer.parseInt(formModule.getSetVersion()));
                cspManagement.setModuleVersionId(moduleVersion.getId());
                cspManagement.setDateChanged(JodaConverter.getCurrentJodaString());
                cspManagementRepository.save(cspManagement);
            }
            else{
                cspManagementRepository.removeByCspIdAndModuleId(cspId, formModule.getModuleId());
            }
        }


        Response response = new Response(HttpStatusResponseType.DATA_DASHBOARD_MANAGE_OK.code(), HttpStatusResponseType.DATA_DASHBOARD_MANAGE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
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

            row.setBtn("<a class=\"btn btn-xs btn-success\" title=\"Delete CSP: " + csp.getName() + "\" href=\""+PAGES_CSP_UPDATE+"?cspId=" + csp.getId() + "\"><i class=\"fa fa-edit\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-default csp-delete\" title=\"Delete CSP: " + csp.getName() + "\" data-csp-id=\"" + csp.getId() + "\" data-csp-last-report=" + cspInfoRepository.findTop1ByOrderByRecordDateTimeDesc().getRecordDateTime()  + " href=\"#\"><i class=\"fa fa-remove\"></i></a>");

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
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_CSP_SAVE_INCONSISTENT_CONTACT.code(), HttpStatusResponseType.DATA_CSP_SAVE_INCONSISTENT_CONTACT.text(), "");
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        //Check if csp_id exists
        if (cspRepository.findOne(cspForm.getCspId()) != null) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.code(), HttpStatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.text(), "");
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
            List<CspInfo> cspInfoList = cspInfoRepository.findByCspId(cspId);
            for (CspInfo cspInfo : cspInfoList) {
                cspModuleInfoRepository.removeByCspInfoId(cspInfo.getId());
            }
            cspInfoRepository.removeByCspId(cspId);
            cspManagementRepository.removeByCspId(cspId);
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
            row.setStartPriority(module.getStartPriority());

            row.setIsDefault("<i class=\"fa fa-square-o\"></i>");
            if (module.getIsDefault() == 1) {
                row.setIsDefault("<i class=\"fa fa-square text-success\"></i>");
            }

            row.setVersionsCount(moduleVersionRepository.countByModuleId(module.getId()));

            String disabled = "";
            if (cspManagementRepository.findByModuleId(module.getId()).size() != 0) {
                disabled = " disabled ";
            }
            row.setBtn("<a class=\"btn btn-xs btn-primary\" title=\"View Versions of Module: " + module.getName() + "\" href=\"" + PAGES_MODULE_VERSION_LIST + "?moduleId=" + module.getId() + "\"><i class=\"fa fa-bars\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-success " + disabled + " \" title=\"Edit Module: " + module.getName() + "\" href=\"" + PAGES_MODULE_UPDATE + "?moduleId=" + module.getId() + "\"><i class=\"fa fa-edit\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-default module-delete " + disabled + " \" + title=\"Delete Module: " + module.getName() + "\" data-module-name=\"" + module.getName() + "\" data-module-id=\"" + module.getId() + "\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }

    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_SAVE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerModule(@RequestBody ModuleForm moduleForm) {
        LOG.info(DATA_BASEURL + DATA_MODULE_SAVE + ": POST received");

        try {
            Module module = new Module();
            module = this.persistModule(module, moduleForm);

            LOG.info(module.getId().toString());

            Response response = new Response(HttpStatusResponseType.DATA_MODULE_SAVE_OK.code(), HttpStatusResponseType.DATA_MODULE_SAVE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_SAVE_RECORD_EXISTS.code(), HttpStatusResponseType.DATA_MODULE_SAVE_RECORD_EXISTS.text(), e.toString());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }
    }

    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_UPDATE + "/{moduleId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity updateModule(@PathVariable Long moduleId, @RequestBody ModuleForm moduleForm) {
        LOG.info(DATA_BASEURL + DATA_MODULE_UPDATE + ": POST received");

        try {
            Module module = moduleRepository.findOne(moduleId);
            module = this.persistModule(module, moduleForm);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_UPDATE_ERROR.code(), HttpStatusResponseType.DATA_MODULE_UPDATE_ERROR.text(), e.toString());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }


        Response response = new Response(HttpStatusResponseType.DATA_MODULE_SAVE_OK.code(), HttpStatusResponseType.DATA_MODULE_SAVE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_REMOVE + "/{moduleId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeModule(@PathVariable Long moduleId) {
        LOG.info(DATA_BASEURL + DATA_MODULE_REMOVE  + "/" + moduleId + ": POST received");

        try {
            /**
             * @TODO Check if removal can be done by management table
             */
            //delete module version
            moduleRepository.delete(moduleId);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_DELETE_ERROR.code(), HttpStatusResponseType.DATA_MODULE_DELETE_ERROR.text(), e.toString());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        Response response = new Response(HttpStatusResponseType.DATA_MODULE_DELETE_OK.code(), HttpStatusResponseType.DATA_MODULE_DELETE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




    /*
    Module Versions
     */
    //PAGES_MODULE_VERSION_LIST
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION + "/{moduleId}.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity moduleVersions(@PathVariable Long moduleId) {
        LOG.info(DATA_BASEURL + DATA_MODULE_VERSION + "/" + moduleId + ".json: POST received");

        List<ModuleVersionRow> rows = new ArrayList<>();
        List<ModuleVersion> moduleVersions = moduleVersionRepository.findByModuleId(moduleId);

        for (ModuleVersion moduleVersion : moduleVersions) {
            ModuleVersionRow row = new ModuleVersionRow();

            row.setIcon("<i class=\"fa fa-file-code-o\"></i>");
            row.setFullName(moduleVersion.getFullName());
            row.setVersion(VersionParser.toString(moduleVersion.getVersion()));
            row.setReleasedOn(moduleVersion.getReleasedOn());
            row.setHash(moduleVersion.getHash());
            row.setDescription(moduleVersion.getDescription());

            String disabled = "";
            if (cspManagementRepository.findByModuleIdAndModuleVersionId(moduleId, moduleVersion.getId()).size() != 0) {
                disabled = " disabled ";
            }
            row.setBtn("<a class=\"btn btn-xs btn-success " + disabled + " \" title=\"Edit Module Version: " + moduleVersion.getFullName() + "\" href=\"" + PAGES_MODULE_VERSION_UPDATE + "?moduleVersionId=" + moduleVersion.getId() + "\"><i class=\"fa fa-edit\"></i></a>"+
                    "&nbsp;"+
                    "<a class=\"btn btn-xs btn-default module-version-delete " + disabled + " \" + title=\"Delete Module Version: " + moduleVersion.getFullName() + "\" data-module-version-name=\"" + moduleVersion.getFullName() + "\" data-module-version-id=\"" + moduleVersion.getId() + "\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        return new ResponseEntity<>(rows, HttpStatus.OK);
    }

    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION_SAVE + "/{moduleId}",
            headers=("content-type=multipart/*"),
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerModuleVersion(@PathVariable Long moduleId,
                                                @RequestParam("module_full_name") String fullName,
                                                @RequestParam("module_version") String version,
                                                @RequestParam("module_file") MultipartFile uploadFile,
                                                @RequestParam("module_description") String description) {
        LOG.info(DATA_BASEURL + DATA_MODULE_VERSION_SAVE + "/" + moduleId + ": POST received");

        if (uploadFile.isEmpty()) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_EMPTY_FILE.code(), HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_EMPTY_FILE.text(), "");
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        try {
            String hash = FileHelper.saveUploadedFile(fileTemp, fileRepository, uploadFile, digestAlgorithm);

            //save after all exceptions
            ModuleVersion moduleVersion = new ModuleVersion();
            moduleVersion.setModuleId(moduleId);
            moduleVersion.setFullName(fullName);
            moduleVersion.setVersion(VersionParser.fromString(version));
            moduleVersion.setReleasedOn(JodaConverter.getCurrentJodaString());
            moduleVersion.setHash(hash);
            moduleVersion.setDescription(description);
            moduleVersionRepository.save(moduleVersion);

            /**
             * @TODO Check if {@link ModuleVersion} exists????
             *
             * @TODO Check if {@link Module} exists??? We operate Module and ModuleVersion from single form, shall Module name not be Unique?
             */
            Response response = new Response(HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_OK.code(), HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException || e instanceof IOException || e instanceof NoSuchFileException) {
                ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_HASH_FILE.code(), HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_HASH_FILE.text(), e.toString());
                return new ResponseEntity<>(error, HttpStatus.OK);
            }
            else {
                ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_UNIQUE.code(), HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_UNIQUE.text(), "");
                return new ResponseEntity<>(error, HttpStatus.OK);
            }
        }
    }


    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION_UPDATE + "/{moduleVersionId}",
            headers=("content-type=multipart/*"),
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity updateModuleVersion(@PathVariable Long moduleVersionId,
                                              @RequestParam("module_id") Long moduleId,
                                              @RequestParam("module_full_name") String fullName,
                                              @RequestParam("module_version") String version,
                                              @RequestParam(value = "module_file", required = false) MultipartFile uploadFile,
                                              @RequestParam("description") String description) {
        LOG.info(DATA_BASEURL + DATA_MODULE_VERSION_UPDATE + "/" + moduleVersionId + ": POST received");

        try {
            ModuleVersion moduleVersion = moduleVersionRepository.findOne(moduleVersionId);
            if (!uploadFile.isEmpty()) {
                String hash = moduleVersionRepository.findOne(moduleVersionId).getHash();
                String fileName = FileHelper.getFileFromHash(fileRepository, hash);
                FileHelper.removeFile(fileRepository, fileName);

                String newHash = FileHelper.saveUploadedFile(fileTemp, fileRepository, uploadFile, digestAlgorithm);
                moduleVersion.setHash(newHash);
            }

            moduleVersion.setModuleId(moduleId);
            moduleVersion.setFullName(fullName);
            moduleVersion.setVersion(VersionParser.fromString(version));
            moduleVersion.setDescription(description);
            moduleVersionRepository.save(moduleVersion);

        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_VERSION_UPDATE_ERROR.code(), HttpStatusResponseType.DATA_MODULE_VERSION_UPDATE_ERROR.text(), e.toString());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }


        Response response = new Response(HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_OK.code(), HttpStatusResponseType.DATA_MODULE_VERSION_SAVE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION_REMOVE + "/{moduleVersionId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeModuleVersion(@PathVariable Long moduleVersionId) {
        LOG.info(DATA_BASEURL + DATA_MODULE_VERSION_REMOVE  + "/" + moduleVersionId + ": POST received");

        try {
            /**
             * @TODO Check if removal can be done by management table
             */
            //delete file
            String hash = moduleVersionRepository.findOne(moduleVersionId).getHash();
            String fileName = FileHelper.getFileFromHash(fileRepository, hash);
            FileHelper.removeFile(fileRepository, fileName);

            //delete module version
            moduleVersionRepository.delete(moduleVersionId);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatusResponseType.DATA_MODULE_DELETE_ERROR.code(), HttpStatusResponseType.DATA_MODULE_DELETE_ERROR.text(), e.toString());
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        Response response = new Response(HttpStatusResponseType.DATA_MODULE_DELETE_OK.code(), HttpStatusResponseType.DATA_MODULE_DELETE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    private Module persistModule(Module module, ModuleForm moduleForm) {
        module.setName(moduleForm.getShortName());
        module.setStartPriority(moduleForm.getStartPriority());

        module.setIsDefault(0);
        //if (moduleForm.getIsDefault().equals("on") || moduleForm.getIsDefault().equals("1") || moduleForm.getIsDefault().equals("true")) {
        if (moduleForm.getIsDefault() != null && moduleForm.getIsDefault()) {
            module.setIsDefault(1);
        }

        module = moduleRepository.save(module);

        return module;
    }
}
