package com.intrasoft.csp.regrep.service.impl;

import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.service.Basis;
import com.intrasoft.csp.regrep.service.RegularReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.intrasoft.csp.regrep.service.Basis.*;

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

        // Populate the basis list depending on the placeholder values from the resource file.
        // Service will not send any emails for any basis types having false values.
        basisList = new ArrayList<>();

        if (daily)
            basisList.add(DAILY);
        if (weekly)
            basisList.add(WEEKLY);
        if (monthly)
            basisList.add(MONTHLY);
        if (quarterly)
            basisList.add(QUARTERLY);
        if (yearly)
            basisList.add(YEARLY);

        LOG.info(String.format("Regular Reports Service Initialized %s", basisList.toString()));

    }

    @Scheduled(cron="${regrep.cron.daily}")
    @Override
    public void reportDaily() {
        if (basisList.contains(DAILY))
          report(DAILY);
    }

    @Scheduled(cron="${regrep.cron.weekly}")
    @Override
    public void reportWeekly() {
        if (basisList.contains(WEEKLY))
          report(WEEKLY);
    }

    @Scheduled(cron="${regrep.cron.monthly}")
    @Override
    public void reportMonthly() {
        if (basisList.contains(MONTHLY))
          report(MONTHLY);
    }

    @Scheduled(cron="${regrep.cron.quarterly}")
    @Override
    public void reportQuarterly() {
        if (basisList.contains(QUARTERLY))
          report(QUARTERLY);
    }

    @Scheduled(cron="${regrep.cron.yearly}")
    @Override
    public void reportYearly() {
        if (basisList.contains(YEARLY))
          report(YEARLY);
    }

    @Override
    public void report(Basis basis) {

        LOG.info(String.format("%s Report", basis));

    }
}
