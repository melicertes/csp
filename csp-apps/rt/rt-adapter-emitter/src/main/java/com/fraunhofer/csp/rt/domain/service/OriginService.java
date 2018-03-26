package com.fraunhofer.csp.rt.domain.service;

import java.util.List;

import com.fraunhofer.csp.rt.domain.model.Origin;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public interface OriginService {

	public List<Origin> getAll();

	public Origin saveOrUpdate(Origin origin);

	List<Origin> findByApplicationId(String applicationId);

	List<Origin> findByCspId(String cspId);

	List<Origin> findByRecordId(String recordId);

	List<Origin> findByOriginRecordId(String uuid);
}
