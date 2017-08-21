package com.intrasoft.csp.conf.server.repository;


import com.intrasoft.csp.conf.server.domain.entities.ModuleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleVersionRepository extends JpaRepository<ModuleVersion, Long> {

    public List<ModuleVersion> findByModuleId(Long moduleId);

    public ModuleVersion findByModuleIdAndVersion(Long moduleId, Integer version);

    public ModuleVersion findByFullName(String fullName);

    public ModuleVersion findByHash(String hash);

    Long countByModuleId(Long moduleId);

    @Query("select MV.version from ModuleVersion MV where MV.moduleId = :moduleId order by MV.version asc")
    List<Integer> findVersionsByModuleId(@Param("moduleId") Long moduleId);
}
