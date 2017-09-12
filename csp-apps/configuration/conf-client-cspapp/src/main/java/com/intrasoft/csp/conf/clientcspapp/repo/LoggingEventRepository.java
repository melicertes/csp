package com.intrasoft.csp.conf.clientcspapp.repo;

import com.intrasoft.csp.conf.clientcspapp.model.LoggingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by tangelatos on 12/09/2017.
 */
@Repository
public interface LoggingEventRepository extends JpaRepository<LoggingEvent, Integer> {


}
