package com.intrasoft.csp.anon.server.repository;

import com.intrasoft.csp.anon.server.model.SecretKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretKeyRepository extends JpaRepository<SecretKey, Long> {
}
