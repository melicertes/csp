package com.intrasoft.csp.conf.clientcspapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.clientcspapp.context.ContextUrl;
import com.intrasoft.csp.conf.clientcspapp.model.ModuleState;
import com.intrasoft.csp.conf.clientcspapp.model.SystemModule;
import com.intrasoft.csp.conf.clientcspapp.model.UpdateVersion;
import com.intrasoft.csp.conf.clientcspapp.model.forms.RegistrationForm;
import com.intrasoft.csp.conf.clientcspapp.service.InstallationService;
import com.intrasoft.csp.conf.clientcspapp.service.SimpleStorageService;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.api.ModulesInfoDTO;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.api.UpdateInformationDTO;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.commons.utils.JodaConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class RestApiController implements ContextUrl, ApiContextUrl {

    @Autowired
    InstallationService installService;

    @Autowired
    SimpleStorageService storageService;

    @RequestMapping(value = REST_REGISTER_FILES + "/{cspId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseDTO registerFiles(
            @PathVariable String cspId,
            @RequestParam("ca_bundle") MultipartFile bundle,
            @RequestParam("ssl_priv_key") MultipartFile privateKey,
            @RequestParam("ssl_pub_key") MultipartFile publicKey
            ) {
        try {
            String caBundleLocation = storageService.storeFileTemporarily(bundle.getInputStream(), "ca-bundle.crt");
            String sslPrivateKey = storageService.storeFileTemporarily(bundle.getInputStream(), "sslprivate.key");
            String sslPublicKey = storageService.storeFileTemporarily(bundle.getInputStream(), "sslpublic.crt");

            ObjectMapper mapper = new ObjectMapper();

            ResponseDTO resp = new ResponseDTO(StatusResponseType.OK.code(), mapper.writeValueAsString(
                    Arrays.asList(caBundleLocation,sslPrivateKey,sslPublicKey)
            ));
            return resp;
        } catch (IOException e) {
            log.error("IOException {}",e.getMessage(),e);
            return new ResponseDTO(StatusResponseType.FAILURE.code(), "IO Exception: "+e.getMessage());
        }

    }

    @RequestMapping(value = REST_REGISTER + "/{cspId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseDTO register(@PathVariable String cspId, @RequestBody RegistrationForm cspForm) {
        RegistrationDTO cspRegistration = new RegistrationDTO();
        cspRegistration.setName(cspForm.getName());
        cspRegistration.setDomainName(cspForm.getDomainName());
        cspRegistration.setRegistrationDate(JodaConverter.getCurrentJodaString());
        cspRegistration.setExternalIPs(cspForm.getExternalIps());
        cspRegistration.setInternalIPs(cspForm.getInternalIps());
        cspRegistration.setRegistrationIsUpdate(false);
        cspRegistration.setContacts(cspForm.getContactDetails());
        // on registration modules are empty.
        ModulesInfoDTO modulesInfo = new ModulesInfoDTO();
        cspRegistration.setModuleInfo(modulesInfo);

        // todo add smtp


        return installService.registerCsp(cspId,cspRegistration);
    }

    @RequestMapping(value = REST_LOG,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity log() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Object>> mapType = new TypeReference<List<Object>>() {};

        File initialFile = new File("e://0//generated.json");


        //InputStream is = TypeReference.class.getResourceAsStream("/generated.json");
        List<Object> exampleList = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(initialFile);
            exampleList = mapper.readValue(is, mapType);
        } catch (IOException e) {
           // System.out.println(e.getMessage());
        }

        return new ResponseEntity<>(exampleList, HttpStatus.OK);
    }

    @RequestMapping(value = REST_DASHSTATUS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity dashboardStatus() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        final String status = mapper.writeValueAsString(installService.mapInstallationStateToPct());

        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = REST_UPDATESFOUND + "/{cspId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updates(@PathVariable String cspId) {
        final UpdateInformationDTO cspUpdates = installService.queryCspUpdates(cspId);

        if (cspUpdates == null || cspUpdates.getAvailable().size() == 0) {
            return new ResponseEntity(HttpStatus.OK); // no updates.
        } else {
            List<UpdateVersion> list = cspUpdates.getAvailable().values().stream()
                    .flatMap(m -> m.stream())
                    .map( mod -> {
                        final String versionInstalled = installService.findModuleInstalledActiveVersion(mod.getName());

                        SystemModule module = installService.findModuleByHash(mod.getHash());
                        StringBuilder actions = new StringBuilder();

                        if (module == null) { // unknown module! lets "initialize"
                            module = SystemModule.builder()
                                    .moduleState(ModuleState.UNKNOWN)
                                    .name(mod.getName())
                                    .description(mod.getDescription())
                                    .active(false)
                                    .hash(mod.getHash())
                                    .version(mod.getVersion())
                                    .build();

                            module = installService.saveSystemModule(module);
                        }


                        switch (module.getModuleState()) {
                            case UNKNOWN:
                                actions.append(
                                        "&nbsp;<a class=\"btn btn-xs btn-primary\" title=\"Download " + module.getName() + "\" href=\"" + PAGE_DOWNLOADMODULE + "/" + module.getHash() + "\"><i class=\"fa fa-download\"></i></a>"
                                );
                                break;
                            case DOWNLOADING:
                                actions.append(
                                        "&nbsp;<a class=\"btn btn-xs btn-primary\" title=\"Show " + module.getName() + " progress\" href=\"" + PAGE_STATUS + "?moduleId=" + module.getHash() + "\"><i class=\"fa fa-cog fa-spin\"></i></a>"
                                );
                                break;
                            case DOWNLOADED:
                                actions.append(
                                        "&nbsp;<a class=\"btn btn-xs btn-success\" title=\"Install " + module.getName() + "\" href=\"" + PAGE_INSTALLMODULE + "/" + module.getHash() + "\"><i class=\"fa fa-cogs\"></i></a>"
                                );
                                actions.append(
                                        "&nbsp;<a class=\"btn btn-xs btn-danger\" title=\"Delete " + module.getName() + "\" href=\"" + PAGE_DELETEMODULE + "/" + module.getHash() + "\"><i class=\"fa fa-trash\"></i></a>"
                                );
                                break;
                            case INSTALLED:
                                actions.append(
                                        "&nbsp;<a class=\"btn btn-xs btn-warning\" title=\"Re-Install " + module.getName() + "\" href=\"" + PAGE_REINSTALLMODULE + "/" + module.getHash() + "\"><i class=\"fa fa-refresh\"></i></a>"
                                );
                                break;
                            case OBSOLETE:
                                actions.append(
                                        "&nbsp;<a class=\"btn btn-xs btn-primary\" title=\"Delete " + module.getName() + "\" href=\"" + PAGE_DELETEMODULE + "/" + module.getHash() + "\"><i class=\"fa fa-trash\"></i></a>"
                                );
                                break;
                            case REMOVED:
                                break;
                        }


                        final UpdateVersion version = UpdateVersion.builder()
                                .name(mod.getName())
                                .description(mod.getDescription())
                                .version(mod.getVersion())
                                .versionInstalled(versionInstalled == null ? "Not yet installed" : versionInstalled)
                                .hash(mod.getHash())
                                .priority(mod.getStartPriority())
                                .released(mod.getReleased())
                                .btn(actions.toString()).build();
                        return version;
                    }).collect(Collectors.toList());
            return new ResponseEntity(list, HttpStatus.OK);
        }


    }
}
