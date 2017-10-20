package com.intrasoft.csp.conf.server.repository;


import com.intrasoft.csp.conf.server.domain.entities.CspModuleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CspModuleInfoRepository extends JpaRepository<CspModuleInfo, Long> {

    public List<CspModuleInfo> findByCspInfoId(Long cspInfoId);

    public CspModuleInfo findTop1ByCspInfoIdOrderByCspInfoIdDesc(Long cspInfoId);

    @Transactional
    List<CspModuleInfo> removeByCspInfoId(Long cspInfoId);
}
