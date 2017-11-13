package com.intrasoft.csp.misp.domain.service;

import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.repository.OriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OriginService {

    public List<Origin> getAll();
    public Origin saveOrUpdate(Origin origin);
    List<Origin> findByApplicationId(String applicationId);
    List<Origin> findByCspId(String cspId);
    List<Origin> findByRecordId(String recordId);
    List<Origin> findByRecordUuid(String uuid);
}
