package com.intrasoft.csp.ccs.server.repository;

import com.intrasoft.csp.ccs.server.domain.entities.CspIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CspIpRepository extends JpaRepository<CspIp, Long> {

    public List<CspIp> findByCspId(String cspId);

    @Query("select cspip.ip from CspIp cspip where cspip.cspId = :cspId and cspip.external = :isExternal")
    List<String> findByCspIdAndExternal(@Param("cspId") String cspId, @Param("isExternal") Integer isExternal);

    @Transactional
    List<CspIp> removeByCspId(String cspId);

}