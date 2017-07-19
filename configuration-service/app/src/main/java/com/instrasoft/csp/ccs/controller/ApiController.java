package com.instrasoft.csp.ccs.controller;

import com.instrasoft.csp.ccs.config.ApiContextUrl;
import com.instrasoft.csp.ccs.config.HttpStatusResponseType;
import com.instrasoft.csp.ccs.domain.api.*;
import com.instrasoft.csp.ccs.domain.postgresql.*;
import com.instrasoft.csp.ccs.repository.*;
import com.instrasoft.csp.ccs.utils.FileHelper;
import com.instrasoft.csp.ccs.utils.JodaConverter;
import com.instrasoft.csp.ccs.utils.VersionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class ApiController implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ApiController.class);

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
        String logInfo = "/v" + API_V1 + API_UPDATES + "/" + cspId + ": ";
        LOG.info(logInfo + "GET received");

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
            updateInformation.setDateChanged("2017-06-25T20:30:54.844Z");

            //get modules by priority, DESC as HashMap reverses the Order
            List<Module> moduleList = moduleRepository.findAll(new Sort(Sort.Direction.ASC, "StartPriority"));
            for (Module module : moduleList) {
                List<ModuleUpdateInfo> updates = new ArrayList<>();
                List<CspManagement> cspManagementList = cspManagementRepository.findByCspIdAndModuleId(cspId, module.getId());
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

            updateInformation.setAvailable(available);

            LOG.info(logInfo + HttpStatusResponseType.API_UPDATES_OK.text());
            return new ResponseEntity<>(updateInformation, HttpStatus.OK);

        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) {
                LOG.error(logInfo + HttpStatusResponseType.API_UPDATES_NOT_FOUND.text() + "; " + HttpStatusResponseType.API_UPDATE_NOT_FOUND.exception());
                ResponseError error = new ResponseError(HttpStatusResponseType.API_UPDATES_NOT_FOUND.code(),
                        HttpStatusResponseType.API_UPDATES_NOT_FOUND.text(), e.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            LOG.error(logInfo + HttpStatusResponseType.API_UPDATES_FAILURE.text() + "; " + HttpStatusResponseType.API_UPDATES_FAILURE.exception());
            ResponseError error = new ResponseError(HttpStatusResponseType.API_UPDATES_FAILURE.code(),
                    HttpStatusResponseType.API_UPDATES_FAILURE.text(), e.toString());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
                LOG.error(logInfo + HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.text() + "; " + HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.exception());
                ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.code(),
                        HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.text(), HttpStatusResponseType.API_REGISTER_NOT_UPDATABLE.exception());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
            else {
                LOG.error(logInfo + HttpStatusResponseType.API_REGISTER_INVALID_CSP_ENTRY.text() + "; " + HttpStatusResponseType.API_REGISTER_INVALID_CSP_ENTRY.exception());
                ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_INVALID_CSP_ENTRY.code(),
                        HttpStatusResponseType.API_REGISTER_INVALID_CSP_ENTRY.text(), HttpStatusResponseType.API_REGISTER_INVALID_CSP_ENTRY.exception());
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
            List<ModuleInfo> moduleInfoList = cspRegistration.getModuleInfo().getModules();
            for(ModuleInfo moduleInfo : moduleInfoList) {
                /*
                Check for errors
                 */
                Module module = moduleRepository.findByName(moduleInfo.getName());
                if (module == null) {
                    LOG.error(logInfo + HttpStatusResponseType.API_REGISTER_INVALID_MODULE_HASH.text() + "; " + HttpStatusResponseType.API_REGISTER_INVALID_MODULE_NAME.exception());
                    ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_INVALID_MODULE_NAME.code(),
                            HttpStatusResponseType.API_REGISTER_INVALID_MODULE_NAME.text(), HttpStatusResponseType.API_REGISTER_INVALID_MODULE_NAME.exception());
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
                ModuleVersion moduleVersion = moduleVersionRepository.findByFullName(moduleInfo.getAdditionalProperties().getFullName());
                if (moduleVersion == null) {
                    LOG.error(logInfo + HttpStatusResponseType.API_REGISTER_INVALID_MODULE_VERSION.text() + "; " + HttpStatusResponseType.API_REGISTER_INVALID_MODULE_VERSION.exception());
                    ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_INVALID_MODULE_VERSION.code(),
                            HttpStatusResponseType.API_REGISTER_INVALID_MODULE_VERSION.text(), HttpStatusResponseType.API_REGISTER_INVALID_MODULE_VERSION.exception());
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
                }
                moduleVersion = moduleVersionRepository.findByHash(moduleInfo.getAdditionalProperties().getHash());
                if (moduleVersion == null) {
                    LOG.error(logInfo + HttpStatusResponseType.API_REGISTER_INVALID_MODULE_HASH.text() + "; " + HttpStatusResponseType.API_REGISTER_INVALID_MODULE_HASH.exception());
                    ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_INVALID_MODULE_HASH.code(),
                            HttpStatusResponseType.API_REGISTER_INVALID_MODULE_HASH.text(), HttpStatusResponseType.API_REGISTER_INVALID_MODULE_HASH.exception());
                    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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

                LOG.info(logInfo + HttpStatusResponseType.API_REGISTER_OK.text());
            }

        } catch (Exception e) {
            LOG.error(logInfo + HttpStatusResponseType.API_REGISTER_FAILURE.text() + "; " + HttpStatusResponseType.API_REGISTER_FAILURE.exception());
            ResponseError error = new ResponseError(HttpStatusResponseType.API_REGISTER_FAILURE.code(), HttpStatusResponseType.API_REGISTER_FAILURE.text(), e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }


        Response response = new Response(HttpStatusResponseType.API_REGISTER_OK.code(), HttpStatusResponseType.API_REGISTER_OK.text());
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
                LOG.error(logInfo + HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text() + "; " + HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.exception());
                ResponseError error = new ResponseError(HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.code(),
                        HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text(), HttpStatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.exception());
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }

            File updateFile = new File(fileRepository + FileHelper.getFileFromHash(fileRepository, updateHash));

            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment; filename=\"" + updateFile.getName() + "\"");

            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(updateFile));

            LOG.info(logInfo + HttpStatusResponseType.API_UPDATE_OK.text());
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(inputStreamResource.contentLength())
                    .contentType(MediaType.parseMediaType(fileMediaType))
                    .body(new InputStreamResource(new FileInputStream(updateFile)));

        } catch (Exception e) {
            if (e instanceof EntityNotFoundException) {
                LOG.error(logInfo + HttpStatusResponseType.API_UPDATE_INVALID_CSP_ENTRY.text() + "; " + HttpStatusResponseType.API_UPDATE_INVALID_CSP_ENTRY.exception());
                ResponseError error = new ResponseError(HttpStatusResponseType.API_UPDATE_INVALID_CSP_ENTRY.code(),
                        HttpStatusResponseType.API_UPDATE_INVALID_CSP_ENTRY.text(), e.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            if (e instanceof FileNotFoundException) {
                LOG.error(logInfo + HttpStatusResponseType.API_UPDATE_NOT_FOUND.text() + "; " + HttpStatusResponseType.API_UPDATE_NOT_FOUND.exception());
                ResponseError error = new ResponseError(HttpStatusResponseType.API_UPDATE_NOT_FOUND.code(),
                        HttpStatusResponseType.API_UPDATE_NOT_FOUND.text(), e.toString());
                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
            }
            LOG.error(logInfo + HttpStatusResponseType.API_UPDATE_FAILURE.text() + "; " + HttpStatusResponseType.API_UPDATE_FAILURE.exception());
            ResponseError error = new ResponseError(HttpStatusResponseType.API_UPDATE_FAILURE.code(), HttpStatusResponseType.API_UPDATE_FAILURE.text(), e.getMessage());
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
