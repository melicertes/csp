package com.instrasoft.csp.ccs.repository;

import com.instrasoft.csp.ccs.domain.postgresql.ModuleVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleVersionRepository extends JpaRepository<ModuleVersion, Long> {

    public ModuleVersion findByModuleId(Long moduleId);
}
