package com.fraunhofer.csp.rt.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fraunhofer.csp.rt.domain.model.Origin;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Repository
public interface OriginRepository extends JpaRepository<Origin, Long> {

	List<Origin> findByOriginApplicationId(String originApplicationId);

	List<Origin> findByOriginCspId(String originCspId);

	List<Origin> findByOriginRecordId(String originRecordId);
}
