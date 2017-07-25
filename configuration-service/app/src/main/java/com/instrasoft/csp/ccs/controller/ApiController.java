package com.instrasoft.csp.ccs.controller;

import com.instrasoft.csp.ccs.config.context.ApiContextUrl;
import com.instrasoft.csp.ccs.config.types.HttpStatusResponseType;
import com.instrasoft.csp.ccs.config.exception.api.*;
import com.instrasoft.csp.ccs.domain.api.*;
import com.instrasoft.csp.ccs.domain.postgresql.*;
import com.instrasoft.csp.ccs.repository.*;
import com.instrasoft.csp.ccs.utils.FileHelper;
import com.instrasoft.csp.ccs.utils.JodaConverter;
import com.instrasoft.csp.ccs.utils.JsonPrinter;
import com.instrasoft.csp.ccs.utils.VersionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class ApiController implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);
    private static Logger LOG_AUDIT = LoggerFactory.getLogger("audit-log");
    private static Logger LOG_EXCEPTION = LoggerFactory.getLogger("exc-log");

    @Value("${server.file.mediaType}")
    String fileMediaType;
    @Value("${server.file.repository}")
    String fileRepository;

    @Autowired
    CspRepository cspRepository;

    @Autowired
    CspIpRepository cspIpRepository;

    @Autowired
    CspContactRepository cspContactRepository;

    @Autowired
    CspInfoRepository cspInfoRepository;

    @Autowired
    CspManagementRepository cspManagementRepository;

    @Autowired
    CspModuleInfoRepository cspModuleInfoRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    ModuleVersionRepository moduleVersionRepository;


    /**
     * Retrieves a list of available updates, for registered modules of the CSP.
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @return ResponseEntity
     */
    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_UPDATES + "/{cspId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updates(@PathVariable String cspId) {
        String user = "system";
        String logInfo = user + ", POST: " + "/v" + API_V1 + API_UPDATES + "/" + cspId + ": ";
        LOG_AUDIT.info(logInfo + "GET received");

        LOG_AUDIT.error("1234");
        LOG_EXCEPTION.info("1234");
        LOG_EXCEPTION.error("1234");


        try {
            //search for CSP
            Csp csp = cspRepository.findOne(cspId);
            if (csp == null) throw new EntityNotFoundException();
            //continue to updates
            LinkedHashMap<String, List<ModuleUpdateInfo>> available = new LinkedHashMap<>();


            UpdateInformation updateInformation = new UpdateInformation();
            /**
             * @TODO DateChanged in CSP Management is not available. If added, it will at entry level.
             */
            updateInformation.setDateChanged(cspManagementRepository.findTop1ByCspIdOrderByDateChangedDesc(cspId).getDateChanged());

            //get modules by priority
            List<Module> moduleList = moduleRepository.findAll(new Sort(Sort.Direction.ASC, "StartPriority"));
            for (Module module : moduleList) {
                List<ModuleUpdateInfo> updates = new ArrayList<>();
                List<CspManagement> cspManagementList = cspManagementRepository.findByCspIdAndModuleId(cspId, module.getId());
                //return only modules having versions
                if (cspManagementList.size() > 0) {
                    for (CspManagement cspManagement : cspManagementList) {
                        ModuleUpdateInfo moduleUpdateInfo = new ModuleUpdateInfo();
                        moduleUpdateInfo.setName(moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getFullName());
                        moduleUpdateInfo.setDescription(moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getDescription());
                        moduleUpdateInfo.setVersion(VersionParser.toString(moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getVersion()));
                        moduleUpdateInfo.setReleased(moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getReleasedOn());
                        moduleUpdateInfo.setReleased(moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getHash());

                        updates.add(moduleUpdateInfo);
                    }

                    available.put(module.getName(), updates);
                }
            }

            updateInformation.setAvailable(available);

            LOG.info(logInfo + HttpStatusResponseType.API_OK.text());
            return new ResponseEntity<>(updateInformation, HttpStatus.OK);

        } catch (Exception e) {
            int code = HttpStatusResponseType.API_FAILURE.code();
            String text = HttpStatusResponseType.API_FAILURE.text();
            HttpStatus status = HttpStatus.BAD_REQUEST;

            if (e instanceof EntityNotFoundException) {
                code = HttpStatusResponseType.API_UPDATES_NOT_FOUND.code();
                text = HttpStatusResponseType.API_UPDATES_NOT_FOUND.text();
                status = HttpStatus.NOT_FOUND;
            }

            LOG.error(logInfo + text + "; " + e.toString());
            ResponseError error = new ResponseError(code, text, e.toString());
            return new ResponseEntity<>(error, status);
        }

    }


    /**
     * Register a NEW csp or register for an existing CSP the modules that are being installed
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @param cspRegistration A block of information to register the CSP being installed
     * @return ResponseEntity
     */
    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_REGISTER + "/{cspId}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity register(@PathVariable String cspId, @RequestBody Registration cspRegistration) {
        String logInfo = "/v" + API_V1 + API_REGISTER + "/" + cspId + ": ";
        LOG.info(logInfo + "POST received");

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
                csp.setId(cspId);
                cspRepository.save(csp);
            }
            else if (cspRepository.exists(cspId) && !cspRegistration.getRegistrationIsUpdate()) {
                throw new CspNotUpdatableException();
            }
            else {
                throw new InvalidCspEntryException();
            }

            //IPs (external and internal)
            cspIpRepository.removeByCspId(cspId);
            this.updateCspIpsFromRegistration(cspId, cspRegistration, 1);
            this.updateCspIpsFromRegistration(cspId, cspRegistration, 0);

            //Contacts
            cspContactRepository.removeByCspId(cspId);
            this.updateCspContactsFromRegistration(cspId, cspRegistration);

            //ModuleInfo
            List<ModuleInfo> moduleInfoList = cspRegistration.getModuleInfo().getModules();
            this.updateModulesInfo(cspId, moduleInfoList);

            LOG.info(logInfo + HttpStatusResponseType.API_OK.text());

        } catch (Exception e) {
            int code = HttpStatusResponseType.API_FAILURE.code();
            String text = HttpStatusResponseType.API_FAILURE.text();
            HttpStatus status = HttpStatus.BAD_REQUEST;

            if (e instanceof CspNotUpdatableException) {
                code = HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.code();
                text = HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidCspEntryException) {
                code = HttpStatusResponseType.API_INVALID_CSP_ENTRY.code();
                text = HttpStatusResponseType.API_INVALID_CSP_ENTRY.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidModuleNameException) {
                code = HttpStatusResponseType.API_INVALID_MODULE_NAME.code();
                text = HttpStatusResponseType.API_INVALID_MODULE_NAME.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidModuleVersionException) {
                code = HttpStatusResponseType.API_INVALID_MODULE_VERSION.code();
                text = HttpStatusResponseType.API_INVALID_MODULE_VERSION.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidModuleHashException) {
                code = HttpStatusResponseType.API_INVALID_MODULE_HASH.code();
                text = HttpStatusResponseType.API_INVALID_MODULE_HASH.text();
                status = HttpStatus.NOT_FOUND;
            }

            LOG.error(logInfo + text + "; " + e.toString());
            ResponseError error = new ResponseError(code, text, e.toString());
            return new ResponseEntity<>(error, status);
        }


        Response response = new Response(HttpStatusResponseType.API_OK.code(), HttpStatusResponseType.API_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Retrieves a list of available updates, for registered modules of the CSP.
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @param updateHash A unique identifier hash for the given update. The system must verify that this hash is
     *                   available for this cspId to download; then it provides the byte stream for this update object.
     * @return ResponseEntity
     */
    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_UPDATE + "/{cspId}" + "/{updateHash}",
            method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE)
    public ResponseEntity update(@PathVariable String cspId, @PathVariable String updateHash) {
        String logInfo = "/v" + API_V1 + API_UPDATE + "/" + cspId + "/" + updateHash + ": ";
        LOG.info(logInfo + "GET received");

        HttpHeaders headers = new HttpHeaders();

        try {
            //search for CSP
            Csp csp = cspRepository.findOne(cspId);
            if (csp == null) throw new EntityNotFoundException();

            //check if CSP is eligible for this update
            Boolean found = false;
            List<CspManagement> cspManagementList = cspManagementRepository.findByCspId(cspId);
            for (CspManagement cspManagement : cspManagementList) {
                if (moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getHash().equals(updateHash)) {
                    found = true;
                }
            }
            if (!found) {
                throw new HashNotFoundException();
            }

            File updateFile = new File(fileRepository + FileHelper.getFileFromHash(fileRepository, updateHash));

            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment; filename=\"" + updateFile.getName() + "\"");

            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(updateFile));

            LOG.info(logInfo + HttpStatusResponseType.API_OK.text());
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(inputStreamResource.contentLength())
                    .contentType(MediaType.parseMediaType(fileMediaType))
                    .body(new InputStreamResource(new FileInputStream(updateFile)));

        } catch (Exception e) {
            int code = HttpStatusResponseType.API_FAILURE.code();
            String text = HttpStatusResponseType.API_FAILURE.text();

            if (e instanceof EntityNotFoundException) {
                code = HttpStatusResponseType.API_UPDATE_INVALID_CSP_ENTRY.code();
                text = HttpStatusResponseType.API_UPDATE_INVALID_CSP_ENTRY.text();
                LOG.error(logInfo + text + "; " + e.toString());
                ResponseError error = new ResponseError(code, text, e.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            if (e instanceof HashNotFoundException) {
                code = HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.code();
                text = HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text();
                LOG.error(logInfo + text + "; " + e.toString());
                ResponseError error = new ResponseError(code, text, e.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            if (e instanceof FileNotFoundException) {
                code = HttpStatusResponseType.API_UPDATE_NOT_FOUND.code();
                text = HttpStatusResponseType.API_UPDATE_NOT_FOUND.text();
                LOG.error(logInfo + text + "; " + e.toString());
                ResponseError error = new ResponseError(code, text, e.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            LOG.error(logInfo + text + "; " + e.toString());
            ResponseError error = new ResponseError(code, text, e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).contentType(MediaType.APPLICATION_JSON).body(error);
        }
    }


    /**
     * Submits a body that contains information of the CSP
     * @param cspId A unique identifier that defines a Registered and Known CSP. The csp identifier follows the UUID
     *              formatted as text, for 36 characters total, arranged as 8-4-4-4-12.
     * @return ResponseEntity
     */
    @RequestMapping(value = API_BASEURL + "/v" + API_V1 + API_APPINFO + "/{cspId}",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity appInfo(@PathVariable String cspId, @RequestBody AppInfo appInfo) {
        String logInfo = "/v" + API_V1 + API_APPINFO + "/" + cspId + ": ";
        LOG.info(logInfo + "POST received");


        System.out.println(JsonPrinter.toJsonPrettyString(appInfo));

        try {
            //search for CSP
            Csp csp = cspRepository.findOne(cspId);
            if (csp == null) throw new EntityNotFoundException();

            //ModuleInfo
            List<ModuleInfo> moduleInfoList = appInfo.getModulesInfo().getModules();
            this.updateModulesInfo(cspId, moduleInfoList);

            LOG.info(logInfo + HttpStatusResponseType.API_OK.text());

        } catch (Exception e) {
            int code = HttpStatusResponseType.API_FAILURE.code();
            String text = HttpStatusResponseType.API_FAILURE.text();
            HttpStatus status = HttpStatus.BAD_REQUEST;

            if (e instanceof EntityNotFoundException) {
                code = HttpStatusResponseType.API_INVALID_CSP_ENTRY.code();
                text = HttpStatusResponseType.API_INVALID_CSP_ENTRY.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidModuleNameException) {
                code = HttpStatusResponseType.API_INVALID_MODULE_NAME.code();
                text = HttpStatusResponseType.API_INVALID_MODULE_NAME.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidModuleVersionException) {
                code = HttpStatusResponseType.API_INVALID_MODULE_VERSION.code();
                text = HttpStatusResponseType.API_INVALID_MODULE_VERSION.text();
                status = HttpStatus.NOT_FOUND;
            }
            else if (e instanceof InvalidModuleHashException) {
                code = HttpStatusResponseType.API_INVALID_MODULE_HASH.code();
                text = HttpStatusResponseType.API_INVALID_MODULE_HASH.text();
                status = HttpStatus.NOT_FOUND;
            }

            LOG.error(logInfo + text + "; " + e.toString());
            ResponseError error = new ResponseError(code, text, e.toString());
            return new ResponseEntity<>(error, status);
        }

        Response response = new Response(HttpStatusResponseType.API_OK.code(), HttpStatusResponseType.API_OK.text());
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    private void updateModulesInfo(String cspId, List<ModuleInfo> moduleInfoList) throws Exception {
        for(ModuleInfo moduleInfo : moduleInfoList) {
            /*
            Check for errors
             */
            Module module = moduleRepository.findByName(moduleInfo.getName());
            if (module == null) {
                throw new InvalidModuleNameException();
            }
            ModuleVersion moduleVersion = moduleVersionRepository.findByFullName(moduleInfo.getAdditionalProperties().getFullName());
            if (moduleVersion == null) {
                throw new InvalidModuleVersionException();
            }
            moduleVersion = moduleVersionRepository.findByModuleIdAndVersion(module.getId(), moduleInfo.getAdditionalProperties().getVersion());
            if (moduleVersion == null) {
                throw new InvalidModuleVersionException();
            }
            moduleVersion = moduleVersionRepository.findByHash(moduleInfo.getAdditionalProperties().getHash());
            if (moduleVersion == null) {
                throw new InvalidModuleHashException();
            }

            /*
            Clear old records, before persisting
             */
            List<CspInfo> cspInfoList = cspInfoRepository.findByCspId(cspId);
            //cspModuleRepository.removeByCspId(cspId);
            for(CspInfo cspInfo : cspInfoList) {
                cspModuleInfoRepository.removeByCspInfoId(cspInfo.getId());
            }
            cspInfoRepository.removeByCspId(cspId);

            /*
            Persist data
             */
            CspInfo cspInfo = new CspInfo();
            cspInfo.setCspId(cspId);
            cspInfo.setRecordDateTime(JodaConverter.getCurrentJodaString());
            cspInfo = cspInfoRepository.save(cspInfo);

//                CspModule cspModule = new CspModule();
//                cspModule.setCspId(cspId);
//                cspModule.setModuleId(module.getId());
//                cspModule.setModuleVersionId(moduleVersion.getId());
//                cspModule = cspModuleRepository.save(cspModule);

            CspModuleInfo cspModuleInfo = new CspModuleInfo();
            cspModuleInfo.setCspInfoId(cspInfo.getId());
            cspModuleInfo.setModuleVersionId(moduleVersion.getId());
            cspModuleInfo.setModuleInstalledOn(moduleInfo.getAdditionalProperties().getInstalledOn());
            int val = moduleInfo.getAdditionalProperties().getActive() ? 1 : 0;
            cspModuleInfo.setModuleIsActive(val);
            cspModuleInfo = cspModuleInfoRepository.save(cspModuleInfo);

        }
    }
}
