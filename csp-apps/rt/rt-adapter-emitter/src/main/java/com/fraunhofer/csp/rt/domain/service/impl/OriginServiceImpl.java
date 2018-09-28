package com.fraunhofer.csp.rt.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fraunhofer.csp.rt.domain.model.Origin;
import com.fraunhofer.csp.rt.domain.repository.OriginRepository;
import com.fraunhofer.csp.rt.domain.service.OriginService;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Service
public class OriginServiceImpl implements OriginService {

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
