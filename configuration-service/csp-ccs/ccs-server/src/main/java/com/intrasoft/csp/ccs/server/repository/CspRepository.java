package com.intrasoft.csp.ccs.server.repository;

import com.intrasoft.csp.ccs.server.domain.postgresql.Csp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CspRepository extends JpaRepository<Csp, String> {
}
