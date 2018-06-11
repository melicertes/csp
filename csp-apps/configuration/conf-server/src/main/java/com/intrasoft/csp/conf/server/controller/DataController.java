package com.intrasoft.csp.conf.server.controller;


import com.intrasoft.csp.conf.commons.exceptions.ConfException;
import com.intrasoft.csp.conf.commons.model.ContactDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.forms.CspForm;
import com.intrasoft.csp.conf.commons.model.forms.ManagementForm;
import com.intrasoft.csp.conf.commons.model.forms.ManagementFormModule;
import com.intrasoft.csp.conf.commons.model.forms.ModuleForm;
import com.intrasoft.csp.conf.commons.types.ContactType;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.commons.utils.JodaConverter;
import com.intrasoft.csp.conf.commons.utils.VersionParser;
import com.intrasoft.csp.conf.server.context.DataContextUrl;
import com.intrasoft.csp.conf.server.context.PagesContextUrl;
import com.intrasoft.csp.conf.server.domain.data.CspRow;
import com.intrasoft.csp.conf.server.domain.data.DashboardRow;
import com.intrasoft.csp.conf.server.domain.data.ModuleRow;
import com.intrasoft.csp.conf.server.domain.data.ModuleVersionRow;
import com.intrasoft.csp.conf.server.domain.entities.*;
import com.intrasoft.csp.conf.server.repository.*;
import com.intrasoft.csp.conf.server.utils.FileHelper;
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
import java.util.zip.ZipFile;

@RestController
public class DataController implements DataContextUrl, PagesContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);
    private static Logger LOG_AUDIT = LoggerFactory.getLogger("audit-log");
    private static Logger LOG_EXCEPTION = LoggerFactory.getLogger("exc-log");

    @Value("${server.file.repository}")
    String fileRepository;
    @Value("${server.file.temp}")
    String fileTemp;
    @Value("${server.digest.algorithm}")
    String digestAlgorithm;
    @Value("${server.manifest}")
    String manifestName;

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

    /**
     * Returns JSON data for dashboard page table
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_DASHBOARD,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity dashboard() {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_DASHBOARD + ": ";
        LOG_AUDIT.info(logInfo + "GET Request received");

        List<DashboardRow> rows = new ArrayList<>();
        List<Csp> csps = cspRepository.findAll();

        for (Csp csp : csps) {
            //get last heartbeat info for current CSP
            CspInfo cspInfo = cspInfoRepository.findTop1ByCspIdOrderByRecordDateTimeDesc(csp.getId());
            CspManagement cspManagement = cspManagementRepository.findTop1ByCspIdOrderByDateChangedDesc(csp.getId());

            DashboardRow row = new DashboardRow();

            row.setIcon("<i class=\"fa fa-cog\"></i>");
            row.setName(csp.getName());
            row.setDomain(csp.getDomainName());
            row.setRegistrationDate(csp.getRegistrationDate());

            //CSP may not have reported its heartbeat, so check it
            row.setLastUpdate("");
            if (cspInfo != null) {
                row.setLastUpdate(cspInfo.getRecordDateTime());
            }

            //CSP may not have been managed yet, so check it
            row.setLastManaged("");
            if (cspManagement != null) {
                row.setLastManaged(cspManagement.getDateChanged());
            }

            List<String> confUpdates = new ArrayList<>();
            List<CspManagement> cspManagements = cspManagementRepository.findByCspId(csp.getId());
            for(CspManagement management : cspManagements) {
                String s;
                s = moduleVersionRepository.findOne(management.getModuleVersionId()).getFullName() + ":" +
                        VersionParser.toString(moduleVersionRepository.findOne(management.getModuleVersionId()).getVersion());
                confUpdates.add(s);
            }
            row.setConfUpdates(confUpdates);

            List<String> reportUpdates = new ArrayList<>();
            //CSP may not have reported its heartbeat, so check it
            if (cspInfo != null) {
                List<CspModuleInfo> cspModuleInfos = cspModuleInfoRepository.findByCspInfoId(cspInfo.getId());
                for(CspModuleInfo cspModuleInfo : cspModuleInfos) {
                    String s;
                    s = moduleVersionRepository.findOne(cspModuleInfo.getModuleVersionId()).getFullName() + ":" +
                            VersionParser.toString(moduleVersionRepository.findOne(cspModuleInfo.getModuleVersionId()).getVersion());
                    reportUpdates.add(s);
                }
            }
            row.setReportUpdates(reportUpdates);

            row.setBtn("<a class=\"btn btn-xs btn-default\" " +
                    "title=\"Manage CSP: " + csp.getName() + " \" " +
                    "href=\"" + PAGES_MANAGE + "?cspId=" + csp.getId() + "\">" +
                    "<i class=\"fa fa-wrench\"></i>" +
                    "</a>");

            rows.add(row);
        }

        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    /**
     * Handles CSP management form
     * @param managementForm    Management object to persist
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MANAGE,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity manage(@RequestBody ManagementForm managementForm) {
        String user = "system";
        String logInfo = user + ", " + "/v" + DATA_BASEURL + DATA_MANAGE + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");


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
            } else {
                cspManagementRepository.removeByCspIdAndModuleId(cspId, formModule.getModuleId());
            }
        }

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_DASHBOARD_MANAGE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_DASHBOARD_MANAGE_OK.code(), StatusResponseType.DATA_DASHBOARD_MANAGE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    /*
    CSP CRUD
     */

    /**
     * Returns JSON data for CSPs page table
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_CSPS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity csps() {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_CSPS + ": ";
        LOG_AUDIT.info(logInfo + "GET Request received");

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

            CspInfo cspInfo = cspInfoRepository.findTop1ByCspIdOrderByRecordDateTimeDesc(csp.getId());
            String cspLastReport = "Not reported yet!";
            if (cspInfo != null) {
                cspLastReport = cspInfo.getRecordDateTime();
            }
            row.setBtn("<a class=\"btn btn-xs btn-success\" title=\"Delete CSP: " + csp.getName() + "\" href=\"" + PAGES_CSP_UPDATE + "?cspId=" + csp.getId() + "\"><i class=\"fa fa-edit\"></i></a>" +
                    "&nbsp;" +
                    "<a class=\"btn btn-xs btn-default csp-delete\" title=\"Delete CSP: " + csp.getName() + "\" data-csp-id=\"" + csp.getId() + "\" data-csp-last-report=\"" + cspLastReport + "\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    /**
     * Handles CSP registration from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param cspForm
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_CSP_SAVE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerCsp(@RequestBody CspForm cspForm) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_CSP_SAVE + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //Check if csp_id exists
        if (cspRepository.exists(cspForm.getCspId())) {
            throw new ConfException(StatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.text(), StatusResponseType.DATA_CSP_SAVE_RECORD_EXISTS.code());
        }

        //Check if contact name, email, type are of equal size
        if ((cspForm.getContactNames().size() + cspForm.getContactEmails().size() + cspForm.getContactTypes().size()) % 3 != 0) {
            throw new ConfException(StatusResponseType.DATA_CSP_SAVE_INVALID_CONTACT.text(), StatusResponseType.DATA_CSP_SAVE_INVALID_CONTACT.code());
        }

        //save csp
        Csp csp = new Csp();
        csp.setId(cspForm.getCspId());
        csp = this.persistCsp(csp, cspForm);

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_CSP_SAVE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_CSP_SAVE_OK.code(), StatusResponseType.DATA_CSP_SAVE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Handles CSP update from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param cspId
     * @param cspForm
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_CSP_UPDATE + "/{cspId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity updateCsp(@PathVariable String cspId, @RequestBody CspForm cspForm) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_CSP_UPDATE + "/" + cspId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //Check if csp_id exists
        if (!cspRepository.exists(cspForm.getCspId())) {
            throw new ConfException(StatusResponseType.DATA_INVALID_CSP_ID.text(), StatusResponseType.DATA_INVALID_CSP_ID.code());
        }

        //remove secondary records
        cspContactRepository.removeByCspId(cspId);
        cspIpRepository.removeByCspId(cspId);

        //persist updated data
        Csp csp = cspRepository.findOne(cspId);
        csp = this.persistCsp(csp, cspForm);

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_CSP_UPDATE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_CSP_UPDATE_OK.code(), StatusResponseType.DATA_CSP_UPDATE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Handles CSP removal from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param cspId
     * @return
     */
    @RequestMapping(value = DATA_BASEURL + DATA_CSP_REMOVE + "/{cspId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeCsp(@PathVariable String cspId) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_CSP_REMOVE + "/" + cspId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //Check if csp_id exists
        if (!cspRepository.exists(cspId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_CSP_ID.text(), StatusResponseType.DATA_INVALID_CSP_ID.code());
        }

        //remove in the opposite order foreign records
        List<CspInfo> cspInfoList = cspInfoRepository.findByCspId(cspId);
        for (CspInfo cspInfo : cspInfoList) {
            cspModuleInfoRepository.removeByCspInfoId(cspInfo.getId());
        }
        cspInfoRepository.removeByCspId(cspId);
        cspManagementRepository.removeByCspId(cspId);
        cspContactRepository.removeByCspId(cspId);
        cspIpRepository.removeByCspId(cspId);

        //remove main record
        cspRepository.delete(cspId);

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_CSP_DELETE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_CSP_DELETE_OK.code(), StatusResponseType.DATA_CSP_DELETE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    /*
    Modules CRUD
     */

    /**
     * Returns JSON data for modules page table
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULES,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity modules() {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULES + ": ";
        LOG_AUDIT.info(logInfo + "GET Request received");

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
            row.setBtn("<a class=\"btn btn-xs btn-primary\" title=\"View Versions of Module: " + module.getName() + "\" href=\"" + PAGES_MODULE_VERSION_LIST + "?moduleId=" + module.getId() + "\"><i class=\"fa fa-bars\"></i></a>" +
                    "&nbsp;" +
                    "<a class=\"btn btn-xs btn-success " + disabled + " \" title=\"Edit Module: " + module.getName() + "\" href=\"" + PAGES_MODULE_UPDATE + "?moduleId=" + module.getId() + "\"><i class=\"fa fa-edit\"></i></a>" +
                    "&nbsp;" +
                    "<a class=\"btn btn-xs btn-default module-delete " + disabled + " \" + title=\"Delete Module: " + module.getName() + "\" data-module-name=\"" + module.getName() + "\" data-module-id=\"" + module.getId() + "\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    /**
     * Handled Module registration from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param moduleForm
     * @return
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_SAVE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerModule(@RequestBody ModuleForm moduleForm) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_SAVE + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //check if Module Name exists
        if (moduleRepository.findByName(moduleForm.getShortName()) != null) {
            throw new ConfException(StatusResponseType.DATA_MODULE_SAVE_NAME_EXISTS.text(), StatusResponseType.DATA_MODULE_SAVE_NAME_EXISTS.code());
        }

        Module module = new Module();
        module = this.persistModule(module, moduleForm);

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_MODULE_SAVE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_MODULE_SAVE_OK.code(), StatusResponseType.DATA_MODULE_SAVE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Handles Module update from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param moduleId
     * @param moduleForm
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_UPDATE + "/{moduleId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity updateModule(@PathVariable Long moduleId, @RequestBody ModuleForm moduleForm) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_UPDATE + "/" + moduleId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //search if module exists
        Module module = moduleRepository.findOne(moduleId);
        if (module == null) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_ID.text(), StatusResponseType.DATA_INVALID_MODULE_ID.code());
        }

        //check if new Module Name exists, when different from original
        if (!module.getName().equals(moduleForm.getShortName()) && moduleRepository.findByName(moduleForm.getShortName()) != null) {
            throw new ConfException(StatusResponseType.DATA_MODULE_SAVE_NAME_EXISTS.text(), StatusResponseType.DATA_MODULE_SAVE_NAME_EXISTS.code());
        }

        //proceed with persisting
        module = this.persistModule(module, moduleForm);

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_MODULE_UPDATE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_MODULE_UPDATE_OK.code(), StatusResponseType.DATA_MODULE_UPDATE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Handles Module removal from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param moduleId
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_REMOVE + "/{moduleId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeModule(@PathVariable Long moduleId) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_REMOVE + "/" + moduleId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //check if Module exists
        if (!moduleRepository.exists(moduleId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_ID.text(), StatusResponseType.DATA_INVALID_MODULE_ID.code());
        }

        //check if removal can be performed, according to management table, on top of UI disabled buttons
        if (cspManagementRepository.findByModuleId(moduleId).size() > 0) {
            throw new ConfException(StatusResponseType.DATA_MODULE_DELETE_ERROR.text(), StatusResponseType.DATA_MODULE_DELETE_ERROR.code());
        }

        //delete module version
        moduleRepository.delete(moduleId);

        LOG_AUDIT.info(logInfo + StatusResponseType.DATA_MODULE_DELETE_OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_MODULE_DELETE_OK.code(), StatusResponseType.DATA_MODULE_DELETE_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    /*
    Module Versions
     */

    /**
     * Returns JSON data for Module Versions page table
     * @param moduleId
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION + "/{moduleId}.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity moduleVersions(@PathVariable Long moduleId) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_VERSION + "/" + moduleId + ".json" + ": ";
        LOG_AUDIT.info(logInfo + "GET Request received");

        //check if Module exists
        if (!moduleRepository.exists(moduleId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_ID.text(), StatusResponseType.DATA_INVALID_MODULE_ID.code());
        }

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
            row.setBtn("<a class=\"btn btn-xs btn-success " + disabled + " \" title=\"Edit Module Version: " + moduleVersion.getFullName() + "\" href=\"" + PAGES_MODULE_VERSION_UPDATE + "?moduleVersionId=" + moduleVersion.getId() + "\"><i class=\"fa fa-edit\"></i></a>" +
                    "&nbsp;" +
                    "<a class=\"btn btn-xs btn-default module-version-delete " + disabled + " \" + title=\"Delete Module Version: " + moduleVersion.getFullName() + "\" data-module-version-name=\"" + moduleVersion.getFullName() + "\" data-module-version-id=\"" + moduleVersion.getId() + "\" href=\"#\"><i class=\"fa fa-remove\"></i></a>");

            rows.add(row);
        }

        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        return new ResponseEntity<>(rows, HttpStatus.OK);
    }


    /**
     * Handles Module Veriosn Registration from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param moduleId
     * @param fullName
     * @param version
     * @param uploadFile
     * @param description
     * @return ResponseEntity
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION_SAVE + "/{moduleId}",
            headers=("content-type=multipart/*"),
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity registerModuleVersion(@PathVariable Long moduleId,
                                                @RequestParam("module_full_name") String fullName,
                                                @RequestParam("module_version") String version,
                                                @RequestParam("module_file") MultipartFile uploadFile,
                                                @RequestParam("module_description") String description) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_VERSION_SAVE + "/" + moduleId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //check if Module exists
        if (!moduleRepository.exists(moduleId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_ID.text(), StatusResponseType.DATA_INVALID_MODULE_ID.code());
        }

        //check for empty file
        if (uploadFile.isEmpty()) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_EMPTY_FILE.text(), StatusResponseType.DATA_MODULE_VERSION_EMPTY_FILE.code());
        }

        //check if module version exists
        if (moduleVersionRepository.findByModuleIdAndVersion(moduleId, VersionParser.fromString(version)) != null) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_EXISTS.text(), StatusResponseType.DATA_MODULE_VERSION_EXISTS.code());
        }

        //save file and get hash
        try {
            String hash = FileHelper.saveUploadedFile(fileTemp, fileRepository, uploadFile, digestAlgorithm);

            //check for manifest.json within archive
            String moduleZipFile = FileHelper.getFileFromHash(fileTemp, hash);
            try (ZipFile zf = new ZipFile(fileTemp + moduleZipFile)) {
                if (zf.getEntry(manifestName) == null) {
                    zf.close();
                    //delete  module zip
                    FileHelper.removeFile(fileTemp, moduleZipFile);
                    throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_INVALID_ARCHIVE.text(), StatusResponseType.DATA_MODULE_VERSION_INVALID_ARCHIVE.code());
                }
            }
            //copy final file to repository
            FileHelper.copyFromTempToRepo(fileTemp, fileRepository, hash);

            //save after all exceptions
            ModuleVersion moduleVersion = new ModuleVersion();
            moduleVersion.setModuleId(moduleId);
            moduleVersion.setFullName(fullName);
            moduleVersion.setVersion(VersionParser.fromString(version));
            moduleVersion.setReleasedOn(JodaConverter.getCurrentJodaString());
            moduleVersion.setHash(hash);
            moduleVersion.setDescription(description);
            moduleVersionRepository.save(moduleVersion);

            LOG_AUDIT.info(logInfo + StatusResponseType.DATA_MODULE_VERSION_SAVE_OK.text());
            ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_MODULE_VERSION_SAVE_OK.code(), StatusResponseType.DATA_MODULE_VERSION_SAVE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_SAVE_FILE.text(), e, StatusResponseType.DATA_MODULE_VERSION_SAVE_FILE.code());
        } catch (NoSuchAlgorithmException e) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_HASH_FILE.text(), e, StatusResponseType.DATA_MODULE_VERSION_HASH_FILE.code());
        }
    }


    /**
     * Handled Module Version update from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param moduleVersionId
     * @param moduleId
     * @param fullName
     * @param version
     * @param uploadFile
     * @param description
     * @return
     */
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
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_VERSION_UPDATE + "/" + moduleVersionId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //check if module version exists
        if (!moduleVersionRepository.exists(moduleVersionId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_VERSION_ID.text(), StatusResponseType.DATA_INVALID_MODULE_VERSION_ID.code());
        }

        //check if module exists
        if (!moduleRepository.exists(moduleId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_ID.text(), StatusResponseType.DATA_INVALID_MODULE_ID.code());
        }

        //check if module full name exists, if different than original
        if (!fullName.equals(moduleVersionRepository.findOne(moduleVersionId).getFullName()) && moduleVersionRepository.findByFullName(fullName) != null) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_NAME_EXISTS.text(), StatusResponseType.DATA_MODULE_VERSION_NAME_EXISTS.code());
        }

        try {
            //do NOT check for empty file, but check for unique hash, upon file uploading
            String newHash = "";
            String fileName = "";
            if (!uploadFile.isEmpty()) {
                String oldHash = moduleVersionRepository.findOne(moduleVersionId).getHash();
                newHash = FileHelper.saveUploadedFile(fileTemp, fileRepository, uploadFile, digestAlgorithm);
                if (moduleVersionRepository.findByHash(newHash) != null) {
                    throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_HASH_EXISTS.text(), StatusResponseType.DATA_MODULE_VERSION_HASH_EXISTS.code());
                }

                //check for manifest.json within archive
                String moduleZipFile = FileHelper.getFileFromHash(fileTemp, newHash);
                ZipFile zf = new ZipFile(fileTemp + moduleZipFile);
                if (zf.getEntry(manifestName) == null) {
                    zf.close();
                    //delete  module zip
                    FileHelper.removeFile(fileTemp, moduleZipFile);
                    throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_INVALID_ARCHIVE.text(), StatusResponseType.DATA_MODULE_VERSION_INVALID_ARCHIVE.code());
                }
                zf.close();

                //copy final file to repository
                FileHelper.copyFromTempToRepo(fileTemp, fileRepository, newHash);
                //find filename from hash and remove old file from file repository
                fileName = FileHelper.getFileFromHash(fileRepository, oldHash);
                FileHelper.removeFile(fileRepository, fileName);
            }



            //proceed with persisting
            ModuleVersion moduleVersion = moduleVersionRepository.findOne(moduleVersionId);
            if (!uploadFile.isEmpty()) {
                moduleVersion.setHash(newHash);
            }
            moduleVersion.setModuleId(moduleId);
            moduleVersion.setFullName(fullName);
            moduleVersion.setVersion(VersionParser.fromString(version));
            moduleVersion.setDescription(description);
            moduleVersionRepository.save(moduleVersion);

            LOG_AUDIT.info(logInfo + StatusResponseType.DATA_MODULE_VERSION_UPDATE_OK.text());
            ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_MODULE_VERSION_UPDATE_OK.code(), StatusResponseType.DATA_MODULE_VERSION_UPDATE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (NoSuchAlgorithmException e) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_HASH_FILE.text(), e.getCause(), StatusResponseType.DATA_MODULE_VERSION_HASH_FILE.code());
        } catch (NoSuchFileException e) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_INVALID_FILE.text(), e.getCause(), StatusResponseType.DATA_MODULE_VERSION_INVALID_FILE.code());
        } catch (IOException e) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_SAVE_FILE.text(), e.getCause(), StatusResponseType.DATA_MODULE_VERSION_SAVE_FILE.code());
        }
    }


    /**
     * Handles Module Version removal from UI. Always return Http Status 200 so as to be easily handled from JS
     * @param moduleVersionId
     * @return
     */
    @RequestMapping(value = DATA_BASEURL + DATA_MODULE_VERSION_REMOVE + "/{moduleVersionId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity removeModuleVersion(@PathVariable Long moduleVersionId) {
        String user = "system";
        String logInfo = user + ", " + DATA_BASEURL + DATA_MODULE_VERSION_REMOVE + "/" + moduleVersionId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //check if module version exists
        if (!moduleVersionRepository.exists(moduleVersionId)) {
            throw new ConfException(StatusResponseType.DATA_INVALID_MODULE_VERSION_ID.text(), StatusResponseType.DATA_INVALID_MODULE_VERSION_ID.code());
        }

        Module module = moduleRepository.findOne(moduleVersionRepository.findOne(moduleVersionId).getModuleId());
        //check if removal can be done by management table
        if (cspManagementRepository.findByModuleIdAndModuleVersionId(module.getId(), moduleVersionId).size() > 0) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_DELETE_ERROR.text(), StatusResponseType.DATA_MODULE_VERSION_DELETE_ERROR.code());
        }

        try {
            //find filename from hash and delete file
            String hash = moduleVersionRepository.findOne(moduleVersionId).getHash();
            String fileName = FileHelper.getFileFromHash(fileRepository, hash);
            FileHelper.removeFile(fileRepository, fileName);

            //delete module version
            moduleVersionRepository.delete(moduleVersionId);

            LOG_AUDIT.info(logInfo + StatusResponseType.DATA_MODULE_VERSION_DELETE_OK.text());
            ResponseDTO response = new ResponseDTO(StatusResponseType.DATA_MODULE_VERSION_DELETE_OK.code(), StatusResponseType.DATA_MODULE_VERSION_DELETE_OK.text());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            throw new ConfException(StatusResponseType.DATA_MODULE_VERSION_INVALID_FILE.text(), e.getCause(), StatusResponseType.DATA_MODULE_VERSION_INVALID_FILE.code());
        }
    }





    private Csp persistCsp(Csp csp, CspForm cspForm) {
        csp.setName(cspForm.getName());
        csp.setDomainName(cspForm.getDomainName());
        csp.setRegistrationDate(JodaConverter.getCurrentJodaString());
        csp = cspRepository.save(csp);

        //save csp contacts
        cspContactRepository.removeByCspId(csp.getId());
        for (ContactDTO contact : cspForm.getContacts()) {
            CspContact cspContact = new CspContact();
            cspContact.setCspId(csp.getId());
            cspContact.setPersonName(contact.getPersonName());
            cspContact.setPersonEmail(contact.getPersonEmail());
            ContactType contactType = ContactType.fromValue(contact.getContactType());
            cspContact.setContactType(contactType);
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
