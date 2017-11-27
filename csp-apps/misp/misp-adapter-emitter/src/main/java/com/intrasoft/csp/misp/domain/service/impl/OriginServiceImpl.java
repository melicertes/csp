package com.intrasoft.csp.misp.domain.service.impl;

import com.intrasoft.csp.misp.domain.model.Origin;
import com.intrasoft.csp.misp.domain.repository.OriginRepository;
import com.intrasoft.csp.misp.domain.service.OriginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OriginServiceImpl implements OriginService{

    @Autowired
    OriginRepository originRepository;

    @Override
    public List<Origin> getAll() {
        return originRepository.findAll();
    }

    @Override
    public Origin saveOrUpdate(Origin origin) {
        return originRepository.save(origin);
    }

    @Override
    public List<Origin> findByApplicationId(String applicationId) {
        return originRepository.findByOriginApplicationId(applicationId);
    }

    @Override
    public List<Origin> findByCspId(String cspId) {
        return originRepository.findByOriginCspId(cspId);
    }

    @Override
    public List<Origin> findByRecordId(String recordId) {
        return originRepository.findByOriginRecordId(recordId);
    }

    @Override
    public List<Origin> findByOriginRecordId(String uuid) {
        return originRepository.findByOriginRecordId(uuid);
    }
}
