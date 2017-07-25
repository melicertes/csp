package com.instasoft.csp.ccs.repository;

import com.instasoft.csp.ccs.domain.postgresql.Csp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CspRepository extends JpaRepository<Csp, String> {
}
