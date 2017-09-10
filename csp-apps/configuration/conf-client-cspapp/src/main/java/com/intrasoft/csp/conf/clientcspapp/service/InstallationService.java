package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemInstallationStateRepository;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemModuleRepository;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.api.UpdateInformationDTO;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public String findModuleInstalledActiveVersion(String moduleName) {
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
    public SystemModule findModuleByHash(String hash) {
        SystemModule module = moduleRepository.findOneByHash(hash);
        if (module != null) {
            log.info("Module {} retrieved!", module);
        }
        return module;
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

    public boolean canDownload() {
        return isInstallationComplete() || isInstallationOngoing();
    }

}
