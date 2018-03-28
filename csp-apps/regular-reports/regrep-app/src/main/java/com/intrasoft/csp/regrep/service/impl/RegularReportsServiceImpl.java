package com.intrasoft.csp.regrep.service.impl;

import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.service.Basis;
import com.intrasoft.csp.regrep.service.RegularReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegularReportsServiceImpl implements RegularReportsService {

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsServiceImpl.class);

/*
    @Autowired
    RegularReportsMailService regularReportsMailService;
*/

    @Autowired
    ElasticSearchClient elasticSearchClient;

    @Value("${regrep.basis.daily}")
    boolean daily;

    @Value("${regrep.basis.weekly}")
    boolean weekly;

    @Value("${regrep.basis.monthly}")
    boolean monthly;

    @Value("${regrep.basis.quarterly}")
    boolean quarterly;

    @Value("${regrep.basis.yearly}")
    boolean yearly;

    private List<Basis> basisList;

    @PostConstruct
    private void init() {

        LOG.info("***** Catched daily: " + String.valueOf(daily));

        basisList = new ArrayList<>();

        if (daily)
            basisList.add(Basis.DAILY);
        if (weekly)
            basisList.add(Basis.WEEKLY);
        if (monthly)
            basisList.add(Basis.MONTHLY);
        if (quarterly)
            basisList.add(Basis.QUARTERLY);
        if (yearly)
            basisList.add(Basis.YEARLY);

        LOG.info(String.format("Regular Reports Service Initialized %s", basisList.toString()));

    }

    @Override
    public void start() {

    }
}
