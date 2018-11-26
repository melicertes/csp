package com.fraunhofer.csp.rt.domain.service;

import java.util.List;

import com.fraunhofer.csp.rt.domain.model.RTuuid;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public interface RTuuidService {

	public List<RTuuid> getAll();

	public RTuuid saveOrUpdate(RTuuid rtuuid);

	List<RTuuid> findByTicketId(String ticketId);

	List<RTuuid> findByRTUuid(String uuid);
}
