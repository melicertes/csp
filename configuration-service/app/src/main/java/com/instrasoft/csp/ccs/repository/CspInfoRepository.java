package com.instrasoft.csp.ccs.repository;

import com.instrasoft.csp.ccs.domain.postgresql.CspInfo;
import com.instrasoft.csp.ccs.domain.postgresql.CspIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CspInfoRepository extends JpaRepository<CspInfo, Long> {

    public List<CspInfo> findByCspId(String cspId);

    //public CspInfo findTop1ByOrderByRecordDateTimeDesc();

    public CspInfo findTop1ByCspIdOrderByRecordDateTimeDesc(String cspId);

    @Transactional
    List<CspInfo> removeByCspId(String cspId);
}
