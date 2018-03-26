package com.fraunhofer.csp.rt.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fraunhofer.csp.rt.domain.model.RTuuid;
import com.fraunhofer.csp.rt.domain.repository.RTuuidRepository;
import com.fraunhofer.csp.rt.domain.service.RTuuidService;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Service
public class RTuuidServiceImpl implements RTuuidService {

	@Autowired
	RTuuidRepository rtUuidRepository;

	@Override
	public List<RTuuid> getAll() {
		return rtUuidRepository.findAll();
	}

	@Override
	public RTuuid saveOrUpdate(RTuuid rtuuid) {
		return rtUuidRepository.save(rtuuid);
	}

	@Override
	public List<RTuuid> findByTicketId(String tid) {
		return rtUuidRepository.findByTid(tid);
	}

	@Override
	public List<RTuuid> findByRTUuid(String uuid) {
		return rtUuidRepository.findByUuid(uuid);
	}
}
