package com.instrasoft.csp.ccs.repository;

import com.instrasoft.csp.ccs.domain.postgresql.CspContact;
import com.instrasoft.csp.ccs.domain.postgresql.CspIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CspContactRepository extends JpaRepository<CspContact, Long> {

    public List<CspContact> findByCspId(String cspId);


    @Transactional
    List<CspContact> removeByCspId(String cspId);

}
