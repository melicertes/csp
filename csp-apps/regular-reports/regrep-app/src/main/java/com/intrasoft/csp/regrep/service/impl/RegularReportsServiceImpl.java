package com.intrasoft.csp.regrep.service.impl;


import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.LogstashMappingType;
import com.intrasoft.csp.regrep.commons.model.Mail;
import com.intrasoft.csp.regrep.service.Basis;
import com.intrasoft.csp.regrep.service.RegularReportsMailService;
import com.intrasoft.csp.regrep.service.RegularReportsService;
import com.intrasoft.csp.regrep.service.RequestBodyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intrasoft.csp.regrep.DateMath.*;
import static com.intrasoft.csp.regrep.service.Basis.*;


@Service
public class RegularReportsServiceImpl implements RegularReportsService {

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsServiceImpl.class);

    @Autowired
    RegularReportsMailService regularReportsMailService;

    @Autowired
    ElasticSearchClient elasticSearchClient;

    @Autowired
    RequestBodyService requestBodyService;

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
        LOG.info(String.format("Sending %s Report...", basis));
        DateMath dateMath = null;
        String requestBody = new String();
        Map<CspDataMappingType, Integer> cspDataResults = new HashMap<>();
        Map<LogstashMappingType, Integer> logstashResults = new HashMap<>();

        // DateMath Mapping
        switch (basis) {
            case DAILY: {
                dateMath = ONE_DAY;
            }
            case WEEKLY: {
                dateMath = ONE_WEEK;
            }
            case MONTHLY: {
                dateMath = ONE_MONTH;
            }
            case QUARTERLY: {
                dateMath = THREE_MONTHS;
            }
            case YEARLY: {
                dateMath = ONE_YEAR;
            }

            // Should be agnostic about the request-body/query content
            default: {
                for (CspDataMappingType cdmt : CspDataMappingType.values()) {
                    requestBody = requestBodyService.buildRequestBody(dateMath, NOW, cdmt);
                    cspDataResults.put(cdmt, elasticSearchClient.getNdocs(requestBody));
                }
                for (LogstashMappingType lmt : LogstashMappingType.values()) {
                    requestBody = requestBodyService.buildRequestBody(dateMath, NOW, lmt);
                    logstashResults.put(lmt, elasticSearchClient.getNlogs(requestBody));
                }
            }
        }
        // TODO: For testing purposes (remove)
        Mail newMail = new Mail();
        newMail.setFrom("giorgosbg@boulougaris.com");
        newMail.setContent("Content");
        newMail.setSubject("Regular Reports");
        newMail.setTo("giorgosbg@outlook.com.gr");
        try {
            regularReportsMailService.sendEmail(newMail);
        } catch (MessagingException e) {
            LOG.error(e.getLocalizedMessage());
        }

    }
}
