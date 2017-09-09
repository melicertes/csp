package com.intrasoft.csp.conf.clientcspapp.repo;

import com.intrasoft.csp.conf.clientcspapp.model.SystemModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by tangelatos on 09/09/2017.
 */
public interface SystemModuleRepository extends JpaRepository<SystemModule,Long> {

    List<SystemModule> findByNameAndActiveOrderByIdDesc(String name, Boolean active);

    SystemModule findOneByHash(String hash);
}
