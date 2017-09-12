package com.intrasoft.csp.conf.clientcspapp.repo;

import com.intrasoft.csp.conf.clientcspapp.model.SystemService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by tangelatos on 11/09/2017.
 */
@Repository
public interface SystemServiceRepository extends JpaRepository<SystemService, Long> {


    SystemService findByName(String serviceName);


}
