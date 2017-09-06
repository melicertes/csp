package com.intrasoft.csp.conf.clientcspapp.repo;

import com.intrasoft.csp.conf.clientcspapp.model.SystemInstallationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Repository
public interface SystemInstallationStateRepository extends JpaRepository<SystemInstallationState, Long> {
}
