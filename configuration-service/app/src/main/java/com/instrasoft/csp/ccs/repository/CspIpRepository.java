package com.instrasoft.csp.ccs.repository;

import com.instrasoft.csp.ccs.domain.postgresql.CspIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CspIpRepository extends JpaRepository<CspIp, Long> {

    public List<CspIp> findByCspId(String cspId);

    @Transactional
    List<CspIp> removeByCspId(String cspId);

}