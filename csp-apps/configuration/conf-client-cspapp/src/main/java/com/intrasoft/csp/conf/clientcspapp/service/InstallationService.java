package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemInstallationStateRepository;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemModuleRepository;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemServiceRepository;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.api.UpdateInformationDTO;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tangelatos on 09/09/2017.
 */
@Service
@Slf4j
public class InstallationService {
    @Autowired
    ConfClient client;

    @Autowired
    SystemInstallationStateRepository repo;

    @Autowired
    SystemModuleRepository moduleRepository;

    @Autowired
    SystemServiceRepository serviceRepository;

    @Autowired
    BackgroundTaskService backgroundTaskService;

    @Autowired
    SimpleStorageService simpleStorage;

    @Transactional
    public ResponseDTO registerCsp(String cspId, RegistrationDTO cspRegistration, SmtpDetails smtp) {
        final ResponseDTO dto = client.register(cspId, cspRegistration);
        if (dto.getResponseCode() == StatusResponseType.OK.code()) {
            log.info("CSP Registration OK, API returned {}",dto.getResponseText());
            //CSP is now registered
            SystemInstallationState state = getState();
            state.setCspId(cspId);
            state.setCspRegistration(cspRegistration);
            state.setInstallationState(InstallationState.IN_PROGRESS);
            state.setSmtpDetails(smtp);
            state = repo.save(state);
            log.info("CSP Registration success! CSP Id: {} [internal:{}]",cspId,state.getId());
        } else {
            log.error("CSP Registration has failed. Error Code {}, Error Text {}", dto.getResponseCode(),dto.getResponseText());
        }
        return dto;
    }

    public UpdateInformationDTO queryCspUpdates(String cspId) {
        SystemInstallationState state = getState();
        if (state.getInstallationState() == InstallationState.NOT_STARTED) {
            log.error("CSP Id not available yet! Install first!");
            return null;
        } else if (!state.getCspId().contentEquals(cspId)) {
            log.error("CSP Id requested {} is not ours ({})", cspId, state.getCspId());
            return null;
        }

        final UpdateInformationDTO updates = client.updates(cspId);
        log.info("Updates retrieved at {} from central {}", updates.getDateChanged(), updates.getAvailable());
        return updates;
    }

    public String queryModuleInstalledActiveVersion(String moduleName) {
        final List<SystemModule> list = moduleRepository.findByNameAndActiveOrderByIdDesc(moduleName, true);

        if (list.size() == 0) {
            return null;
        } else {
            // only one can be active anyway
            return list.get(0).getVersion();
        }
    }


    @Transactional
    public SystemModule saveSystemModule(SystemModule module) {
        module = moduleRepository.save(module);
        log.info("Module {} saved!", module);
        return module;
    }

    @Transactional
    public SystemModule updateSystemModuleState(Long id, ModuleState state) {
        SystemModule module = moduleRepository.findOne(id);
        module.setModuleState(state);

        return moduleRepository.save(module);
    }


    public SystemModule queryModuleByName(String name, boolean active) {
        List<SystemModule> module = moduleRepository.findByNameAndActiveOrderByIdDesc(name, active);
        if (module != null && module.size()==1) {
            log.info("Module list size : {} retrieved!", module.size());
            return module.get(0);
        } else if (module.size() > 0){
            log.error("SYSTEM ERROR; Modules are active: {} cannot have same name and active=true more than once!!!", module);
        } else if (module.size() <= 0) {
            log.error("SYSTEM ERROR; Module with name {} and active {} is not found?",
                    name, active);
        }
        return null;
    }

    public SystemModule queryModuleByHash(String hash) {
        SystemModule module = moduleRepository.findOneByHash(hash);
        if (module != null) {
            log.debug("Module {} retrieved!", module.getName());
        }
        return module;
    }


    @Transactional
    public SystemService updateSystemService(SystemService service) {
        return serviceRepository.save(service);
    }

    @Transactional
    public SystemService saveSystemModuleService(SystemModule module, Boolean legacyMode, boolean needsAgent, boolean needsVhost) {

        SystemModule savedModule = moduleRepository.save(module);

        SystemService systemService = serviceRepository.findByName(module.getName());
        if (systemService == null) {
            systemService = SystemService.builder()
                    .module(savedModule)
                    .serviceState(ServiceState.NOT_RUNNING)
                    .name(module.getName())
                    .build();
        } else {
            systemService.setModule(savedModule);
        }
        systemService.setLegacy(legacyMode);
        systemService.setOamAgentNecessary(needsAgent);
        systemService.setVHostNecessary(needsVhost);
        systemService.setStartable(
                moduleContains(module,"docker-compose.yml") ||
                moduleContains(module, "docker-compose.yml.j2"));
        log.info("Created service {}",systemService);
        return serviceRepository.save(systemService);

    }


    ///// helpers

    public SystemInstallationState getState() {
        final List<SystemInstallationState> list = repo.findAll();
        if (list.size() == 0) {
            return new SystemInstallationState();
        }
        return list.get(0);
    }

    public boolean isInstallationComplete() {
        return getState().getInstallationState() == InstallationState.COMPLETED;
    }
    public boolean isInstallationOngoing() {
        return !isInstallationComplete() && getState().getInstallationState() != InstallationState.NOT_STARTED;
    }


    public Integer mapInstallationStateToPct() {
        SystemInstallationState state = getState();

        if (state.getInstallationState() == InstallationState.NOT_STARTED){
            return 0;
        } else {
            switch (state.getInstallationState()) {
                case NOT_STARTED:
                    return 0;
                case IN_PROGRESS:
                    return 50;
                case COMPLETED:
                    return 100;
                case FAILED:
                    return 90;
            }
        }
        return 95;
    }

    public SystemInstallationState updateSystemInstallationState(SystemInstallationState state) {
        return repo.save(state);
    }


    public boolean canDownload() {
        return isInstallationComplete() || isInstallationOngoing();
    }

    /**
     * find all tar files from this module and upload them in docker.
     * @param module module has the module install path
     *
     * @return true if all was well
     */
    public boolean installDockerImages(SystemModule module) {
        File moduleDir = new File(module.getModulePath());

        final List<BackgroundTaskResult<Boolean, Integer>> list = Stream.of(moduleDir.list((File dir, String name) -> {
            if (name.endsWith("tar")||name.endsWith("bz2")) {
                return true;
            } else
                return false;
        })).map(fName -> {
            Map<String, String> env = new HashMap<>();
            env.put("ARCHIVE_FILE", fName);
            env.put("WORK_DIR", moduleDir.getAbsolutePath());

            try {
                return backgroundTaskService.executeScriptSimple("dockerLoad.sh", env);
            } catch (IOException e) {
                log.error("Exception in load execution: {}", e.getMessage(), e);
                return new BackgroundTaskResult<>(false, -1);
            }
        }).distinct().collect(Collectors.toList());

        for (BackgroundTaskResult<Boolean, Integer> r : list) {
            if (r.getSuccess() == false) {
                return false;
            }
        }
        return true; //not a very nice way to handle errors in returns
    }

    /**
     * return if module contains a specific file
     * @param module
     * @param filename
     * @return
     */
    public Boolean moduleContains(SystemModule module, String filename) {
        return simpleStorage.filesInArchive(module.getArchivePath()).anyMatch( f -> f.contentEquals(filename));
    }

    public List<SystemModule> queryAllModulesInstalled(boolean orderIncreasing) {
        if (orderIncreasing) {
            return moduleRepository.findByModuleStateOrderByStartPriority(ModuleState.INSTALLED);
        } else {
            return moduleRepository.findByModuleStateOrderByStartPriorityDesc(ModuleState.INSTALLED);
        }
    }

    public SystemService queryService(SystemModule module) {
        SystemService service = serviceRepository.findByName(module.getName());
        if (service.getModule().getId().longValue() == module.getId().longValue()) {
            log.debug("Service id {} linked to Module id {}, CORRECT", service.getId(), module.getId());
        } else {
            log.warn("Service id {} linked to Module id {}, but incoming module has id {}",
                    service.getId(), service.getModule().getId(), module.getId());
        }
        return service;
    }

    public SystemService updateServiceState(SystemService service, ServiceState state) {
        SystemService upd = serviceRepository.findByName(service.getName());
        upd.setServiceState(state);
        return serviceRepository.save(upd);
    }

    public List<SystemService> queryCspServices() {
        return serviceRepository.findAll(new Sort(Sort.Direction.ASC, "module.startPriority"));
    }

    public void removeService(SystemService service) {
        serviceRepository.delete(service.getId());
    }

    public List<SystemModule> queryModulesAsServicesInstalled() {

        List<SystemModule> modules = new ArrayList<>();
        serviceRepository.findAll(new Sort(Sort.Direction.ASC,"module.startPriority")).forEach(service -> modules.add(service.getModule()));

        return modules;
    }
}

