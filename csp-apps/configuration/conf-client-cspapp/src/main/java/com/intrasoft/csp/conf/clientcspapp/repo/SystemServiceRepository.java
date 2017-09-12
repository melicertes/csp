package com.intrasoft.csp.conf.clientcspapp.repo;

import com.intrasoft.csp.conf.clientcspapp.model.SystemService;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tangelatos on 11/09/2017.
 */
public interface SystemServiceRepository extends JpaRepository<SystemService, Long> {


    SystemService findByName(String serviceName);


}
