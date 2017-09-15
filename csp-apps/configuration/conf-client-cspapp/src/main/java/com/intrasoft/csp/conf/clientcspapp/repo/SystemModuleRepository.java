package com.intrasoft.csp.conf.clientcspapp.repo;

import com.intrasoft.csp.conf.clientcspapp.model.ModuleState;
import com.intrasoft.csp.conf.clientcspapp.model.SystemModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by tangelatos on 09/09/2017.
 */
@Repository
public interface SystemModuleRepository extends JpaRepository<SystemModule,Long> {

    List<SystemModule> findByNameAndActiveOrderByIdDesc(String name, Boolean active);

    SystemModule findOneByHash(String hash);

    List<SystemModule> findByModuleStateOrderByStartPriority(ModuleState installed);

    List<SystemModule> findByModuleStateOrderByStartPriorityDesc(ModuleState installed);
}
