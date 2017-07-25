package com.instrasoft.csp.ccs.repository;

import com.instrasoft.csp.ccs.domain.postgresql.CspInfo;
import com.instrasoft.csp.ccs.domain.postgresql.CspIp;
import com.instrasoft.csp.ccs.domain.postgresql.CspManagement;
import com.instrasoft.csp.ccs.domain.postgresql.CspModuleInfo;
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
