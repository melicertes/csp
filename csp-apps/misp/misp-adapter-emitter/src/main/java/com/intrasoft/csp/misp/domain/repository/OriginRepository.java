package com.intrasoft.csp.misp.domain.repository;

import com.intrasoft.csp.misp.domain.model.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OriginRepository extends JpaRepository<Origin, Long> {

    List<Origin> findByOriginApplicationId(String originApplicationId);
    List<Origin> findByOriginCspId(String originCspId);
    List<Origin> findByOriginRecordId(String originRecordId);
}
