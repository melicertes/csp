package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.InstallationState;
import com.intrasoft.csp.conf.clientcspapp.model.SystemInstallationState;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemInstallationStateRepository;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ResponseDTO registerCsp(String cspId, RegistrationDTO cspRegistration) {
        final ResponseDTO dto = client.register(cspId, cspRegistration);
        if (dto.getResponseCode() == StatusResponseType.OK.code()) {
            log.info("CSP Registration OK, API returned {}",dto.getResponseText());
            //CSP is now registered


            SystemInstallationState state = getState();
            state.setCspId(cspId);
            state.setCspRegistration(cspRegistration);
            state.setInstallationState(InstallationState.IN_PROGRESS);

            state = repo.save(state);
            log.info("CSP Registration success! CSP Id: {} [internal:{}]",cspId,state.getId());
        } else {
            log.error("CSP Registration has failed. Error Code {}, Error Text {}", dto.getResponseCode(),dto.getResponseText());
        }
        return dto;
    }

}
