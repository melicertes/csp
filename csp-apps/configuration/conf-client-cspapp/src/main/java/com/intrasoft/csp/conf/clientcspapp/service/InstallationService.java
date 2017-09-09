package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.clientcspapp.model.InstallationState;
import com.intrasoft.csp.conf.clientcspapp.model.SystemInstallationState;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemInstallationStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by tangelatos on 09/09/2017.
 */
@Service
public class InstallationService {

    @Autowired
    SystemInstallationStateRepository repo;


    public boolean isInstallationComplete() {

        final List<SystemInstallationState> list = repo.findAll();

        if (list.size() == 0) {
            return false;
        } else {
            return list.get(0).getInstallationState() == InstallationState.COMPLETED;
        }

    }

    public Integer mapInstallationStateToPct() {
        final List<SystemInstallationState> list = repo.findAll();
        if (list.size() == 0) {
            return 0;
        } else {
            switch (list.get(0).getInstallationState()) {
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

}
