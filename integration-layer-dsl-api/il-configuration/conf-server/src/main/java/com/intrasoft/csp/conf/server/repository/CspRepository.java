package com.intrasoft.csp.conf.server.repository;


import com.intrasoft.csp.conf.server.domain.entities.Csp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CspRepository extends JpaRepository<Csp, String> {
}
