package com.intrasoft.csp.ccs.repository;

import com.intrasoft.csp.ccs.domain.postgresql.CspModuleInfo;
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
