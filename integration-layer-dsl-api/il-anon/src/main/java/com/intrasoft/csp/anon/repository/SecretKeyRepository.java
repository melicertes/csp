package com.intrasoft.csp.anon.repository;

import com.intrasoft.csp.anon.model.SecretKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretKeyRepository extends JpaRepository<SecretKey, Long> {
}
