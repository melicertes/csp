package com.fraunhofer.csp.rt.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fraunhofer.csp.rt.domain.model.RTuuid;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Repository
public interface RTuuidRepository extends JpaRepository<RTuuid, Long> {

	List<RTuuid> findByTid(String tid);

	List<RTuuid> findByUuid(String uuid);
}
