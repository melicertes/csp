package com.intrasoft.csp.ccs.repository;

import com.intrasoft.csp.ccs.domain.postgresql.Csp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CspRepository extends JpaRepository<Csp, String> {
}
