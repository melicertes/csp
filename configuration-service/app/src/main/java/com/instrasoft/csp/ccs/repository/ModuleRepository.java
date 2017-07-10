package com.instrasoft.csp.ccs.repository;

import com.instrasoft.csp.ccs.domain.postgresql.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

//    public Module findByName(String name);
//
//    @Transactional
//    Module removeByName(String name);

}

