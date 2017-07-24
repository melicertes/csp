package com.intrasoft.csp.anon.repository;

import com.intrasoft.csp.anon.model.Ruleset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by chris on 10/7/2017.
 */
@Repository
public interface RulesetRepository extends JpaRepository<Ruleset, Long> {


}
