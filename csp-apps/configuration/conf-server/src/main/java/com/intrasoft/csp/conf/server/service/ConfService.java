package com.intrasoft.csp.conf.server.service;

import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.exceptions.*;
import com.intrasoft.csp.conf.commons.interfaces.Configuration;
import com.intrasoft.csp.conf.commons.model.api.*;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.commons.utils.JodaConverter;
import com.intrasoft.csp.conf.commons.utils.VersionParser;
import com.intrasoft.csp.conf.server.domain.entities.*;
import com.intrasoft.csp.conf.server.repository.*;
import com.intrasoft.csp.conf.server.utils.FileHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ConfService implements ApiContextUrl, Configuration {
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

    @Override
    public UpdateInformationDTO updates(String cspId) {
        String user = "system";
        String logInfo = user + ", " + "/v" + REST_API_V1 + API_UPDATES + "/" + cspId + ": ";
        LOG_AUDIT.info(logInfo + "GET Request received");

        //search for CSP
        Csp csp = cspRepository.findOne(cspId);
        if (csp == null) {
            throw new InvalidCspEntryException(StatusResponseType.API_INVALID_CSP_ENTRY.text());
        }

        //continue to updates
        LinkedHashMap<String, List<ModuleUpdateInfoDTO>> available = new LinkedHashMap<>();

        UpdateInformationDTO updateInformation = new UpdateInformationDTO();
        CspManagement cspMgt = cspManagementRepository.findTop1ByCspIdOrderByDateChangedDesc(cspId);
        if (cspMgt == null) {
            throw new InvalidCspEntryException(StatusResponseType.API_CSP_NOT_CONFIGURED_YET.text());
        }
        updateInformation.setDateChanged(cspMgt.getDateChanged());

        //get modules by priority
        List<Module> moduleList = moduleRepository.findAll(new Sort(Sort.Direction.ASC, "StartPriority"));
        for (Module module : moduleList) {
            List<ModuleUpdateInfoDTO> updates = new ArrayList<>();
            List<CspManagement> cspManagementList = cspManagementRepository.findByCspIdAndModuleId(cspId, module.getId());
            //return only modules having versions
            for (CspManagement cspManagement : cspManagementList) {
                ModuleVersion versionManaged = moduleVersionRepository.findOne(cspManagement.getModuleVersionId());
                ModuleUpdateInfoDTO moduleUpdateInfo = new ModuleUpdateInfoDTO();
                moduleUpdateInfo.setName(versionManaged.getFullName());
                moduleUpdateInfo.setDescription(versionManaged.getDescription());
                moduleUpdateInfo.setVersion(VersionParser.toString(versionManaged.getVersion()));
                moduleUpdateInfo.setReleased(versionManaged.getReleasedOn());
                moduleUpdateInfo.setHash(versionManaged.getHash());
                moduleUpdateInfo.setStartPriority(module.getStartPriority());
                updates.add(moduleUpdateInfo);
            }
            available.put(module.getName(), updates);
        }
        updateInformation.setAvailable(available);

        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        return updateInformation;
    }

    @Override
    public ResponseDTO register(String cspId, RegistrationDTO cspRegistration) {
        String user = "system";
        String logInfo = user + ", " + "/v" + REST_API_V1 + API_REGISTER + "/" + cspId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        if (cspRepository.exists(cspId) && cspRegistration.getRegistrationIsUpdate()) {
            // update Csp basic info
            Csp csp = this.getCspFromRegistration(cspRegistration);
            csp.setId(cspId);
            cspRepository.save(csp);
        } else if (!cspRepository.exists(cspId) && !cspRegistration.getRegistrationIsUpdate()) {
            // insert Csp basic info
            Csp csp = this.getCspFromRegistration(cspRegistration);
            csp.setId(cspId);
            cspRepository.save(csp);
        } else if (cspRepository.exists(cspId) && !cspRegistration.getRegistrationIsUpdate()) {
            throw new ConfException(StatusResponseType.API_REGISTER_NOT_UPDATABLE.text(), StatusResponseType.API_REGISTER_NOT_UPDATABLE.code());
        } else {
            throw new ConfException(StatusResponseType.API_INVALID_CSP_ENTRY.text(), StatusResponseType.API_INVALID_CSP_ENTRY.code());
        }

        //IPs (external and internal)
        cspIpRepository.removeByCspId(cspId);
        this.updateCspIpsFromRegistration(cspId, cspRegistration, 1);
        this.updateCspIpsFromRegistration(cspId, cspRegistration, 0);

        //Contacts
        cspContactRepository.removeByCspId(cspId);
        this.updateCspContactsFromRegistration(cspId, cspRegistration);

        //ModuleInfo
        ModulesInfoDTO modulesInfo = cspRegistration.getModuleInfo();
        if (modulesInfo.getModules() != null) {
            this.updateModulesInfo(cspId, modulesInfo.getModules());
        }


        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.OK.code(), StatusResponseType.OK.text());
        return response;
    }

    @Override
    public ResponseEntity update(String cspId, String updateHash) {
        String user = "system";
        String logInfo = user + ", " + "/v" + REST_API_V1 + API_UPDATE + "/" + cspId + "/" + updateHash + ": ";
        LOG_AUDIT.info(logInfo + "GET Request received");

        //search for CSP
        Csp csp = cspRepository.findOne(cspId);
        if (csp == null) {
            throw new InvalidCspEntryException(StatusResponseType.API_INVALID_CSP_ENTRY.text());
        }

        //check if CSP is eligible for this update
        Boolean found = false;
        List<CspManagement> cspManagementList = cspManagementRepository.findByCspId(cspId);
        for (CspManagement cspManagement : cspManagementList) {
            if (moduleVersionRepository.findOne(cspManagement.getModuleVersionId()).getHash().equals(updateHash)) {
                found = true;
            }
        }
        if (!found) {
            throw new UpdateInvalidHashEntryException(StatusResponseType.API_UPDATE_INVALID_HASH_ENTRY.text());
        }

        try {
            File updateFile = new File(fileRepository + FileHelper.getFileFromHash(fileRepository, updateHash));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment; filename=\"" + updateFile.getName() + "\"");
            
            byte[] content = Files.readAllBytes(Paths.get(updateFile.getAbsolutePath()));

            LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(content.length)
                    .contentType(MediaType.parseMediaType(fileMediaType))
                    .body(content);

        } catch (IOException e) {
            throw new UpdateNotFoundException(StatusResponseType.API_UPDATE_NOT_FOUND.text());
        }
    }

    @Override
    public ResponseDTO appInfo(String cspId, AppInfoDTO appInfo) {
        String user = "system";
        String logInfo = user + ", " + "/v" + REST_API_V1 + API_APPINFO + "/" + cspId + ": ";
        LOG_AUDIT.info(logInfo + "POST Request received");

        //search for CSP
        Csp csp = cspRepository.findOne(cspId);
        if (csp == null) {
            throw new InvalidCspEntryException(StatusResponseType.API_INVALID_CSP_ENTRY.text());
        }

        //ModuleInfo
        List<ModuleInfoDTO> moduleInfoList = appInfo.getModulesInfo().getModules();
        this.updateModulesInfo(cspId, moduleInfoList);

        LOG_AUDIT.info(logInfo + StatusResponseType.OK.text());
        ResponseDTO response = new ResponseDTO(StatusResponseType.OK.code(), StatusResponseType.OK.text());
        return response;
    }

    private Csp getCspFromRegistration(RegistrationDTO cspRegistration) {
        Csp csp = new Csp();
        csp.setName(cspRegistration.getName());
        csp.setDomainName(cspRegistration.getDomainName());
        csp.setRegistrationDate(cspRegistration.getRegistrationDate());
        return csp;
    }

    private void updateCspIpsFromRegistration(String cspId, RegistrationDTO cspRegistration, Integer external) {
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

    private void updateCspContactsFromRegistration(String cspId, RegistrationDTO cspRegistration) {
        List<ContactDetailsDTO> contacts = cspRegistration.getContacts();

        for (ContactDetailsDTO contact : contacts) {
            CspContact cspContact = new CspContact();
            cspContact.setCspId(cspId);
            cspContact.setPersonName(contact.getPersonName());
            cspContact.setPersonEmail(contact.getPersonEmail());
            cspContact.setContactType(contact.getContactType());
            cspContactRepository.save(cspContact);
        }
    }

    private void updateModulesInfo(String cspId, List<ModuleInfoDTO> moduleInfoList) {
        /*
        Clear old CspInfo records, before persisting
        */
        List<CspInfo> cspInfoList = cspInfoRepository.findByCspId(cspId);
        //cspModuleRepository.removeByCspId(cspId);
        for (CspInfo info : cspInfoList) {
            cspModuleInfoRepository.removeByCspInfoId(info.getId());
        }
        cspInfoRepository.removeByCspId(cspId);

        /*
        Create CspInfo entry
         */
        CspInfo cspInfo = new CspInfo();
        cspInfo.setCspId(cspId);
        cspInfo.setRecordDateTime(JodaConverter.getCurrentJodaString());
        cspInfo = cspInfoRepository.save(cspInfo);

        for (ModuleInfoDTO moduleInfo : moduleInfoList) {
            /*
            Check for errors
             */
            Module module = moduleRepository.findByName(moduleInfo.getName());
            if (module == null) {
                throw new InvalidModuleNameException(StatusResponseType.API_INVALID_MODULE_NAME.text());
            }

            String fullNameReported = moduleInfo.getAdditionalProperties().getFullName().replace(":", "");
            String versionReportedS = fullNameReported.replace(module.getName(), "");
            Integer versionReportedI;
            //check mistyped json, version from fullname and version mismatch
            try {
                versionReportedI = VersionParser.fromVarious(versionReportedS);
            } catch (NumberFormatException e) {
                throw new InvalidModuleVersionException(StatusResponseType.API_INVALID_MODULE_VERSION.text());
            }
            //before compare versions ensure both have VersionParser's format
            String s1 = String.valueOf(versionReportedI);
            String s2 = String.valueOf(moduleInfo.getAdditionalProperties().getVersion());
            s2 = Integer.toString(VersionParser.fromString(s2));
            if (!s1.equals(s2)) {
                throw new InvalidModuleVersionException(StatusResponseType.API_INVALID_MODULE_VERSION.text());
            }

            //now versions between fullname and version node are aligned. Check for names
            fullNameReported = fullNameReported.replace(versionReportedS, "");
            //check that is the same with module name in json
            if (!fullNameReported.equals(moduleInfo.getName())) {
                throw new InvalidModuleNameException(StatusResponseType.API_INVALID_MODULE_NAME.text());
            }

            //check that module version exists. Ignore reported version mistyping, i.e. 1000 <-> 10000
            /**
             * @TODO If we do not want to ignore version mistype, swap commented lines in next 2-checks
             */
            Integer v = versionReportedI;
            //Integer v = moduleInfo.getAdditionalProperties().getVersion();
            ModuleVersion moduleVersion = moduleVersionRepository.findByFullNameAndVersion(fullNameReported, v);
            if (moduleVersion == null) {
                throw new InvalidModuleVersionException(StatusResponseType.API_INVALID_MODULE_VERSION.text());
            }

            moduleVersion = moduleVersionRepository.findByModuleIdAndVersion(module.getId(), v);
            if (moduleVersion == null) {
                throw new InvalidModuleVersionException(StatusResponseType.API_INVALID_MODULE_VERSION.text());
            }

            moduleVersion = moduleVersionRepository.findByHash(moduleInfo.getAdditionalProperties().getHash());
            if (moduleVersion == null) {
                throw new InvalidModuleHashException(StatusResponseType.API_INVALID_MODULE_HASH.text());
            }

            /*
            Persist data
             */
            CspModuleInfo cspModuleInfo = new CspModuleInfo();
            cspModuleInfo.setCspInfoId(cspInfo.getId());
            cspModuleInfo.setModuleVersionId(moduleVersion.getId());
            cspModuleInfo.setModuleInstalledOn(moduleInfo.getAdditionalProperties().getInstalledOn());
            int val = 0;
            if (moduleInfo.getAdditionalProperties().getActive() != null && moduleInfo.getAdditionalProperties().getActive() == true) {
                val = 1;
            }
            //int val = moduleInfo.getAdditionalProperties().getActive() ? 1 : 0;
            cspModuleInfo.setModuleIsActive(val);
            cspModuleInfo = cspModuleInfoRepository.save(cspModuleInfo);

        }
    }
}
