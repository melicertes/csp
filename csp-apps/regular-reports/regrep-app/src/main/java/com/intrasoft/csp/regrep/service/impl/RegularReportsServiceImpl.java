package com.intrasoft.csp.regrep.service.impl;

import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.ElasticSearchClient;
import com.intrasoft.csp.regrep.LogstashMappingType;
import com.intrasoft.csp.regrep.commons.model.HitsItem;
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
import java.text.SimpleDateFormat;
import java.util.*;

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
// Deprecated
//    @Value("${regrep.from}")
//    String from;

    @Value("${regrep.to}")
    String[] to;

    @Value(value = "${app.mail.sender.name}")
    private String mailFromName;

    @Value(value = "${app.mail.sender.email}")
    private String mailFromMail;

    @Value("${regrep.date.pattern}")
    String datePattern;

    @Value("${th.email.message}")
    String msg;

    @Value("${app.es.logs.exc.limit.size}")
    int excLogsLimitSize;

    @Value("${th.email.es.logs.exc.limit.message}")
    String excLogsLimitMessage;

    String parentheses;

    private List<Basis> basisList;

    @PostConstruct
    private void init() {

        // Populate the basis list depending on the placeholder values from the resource file.
        // Service will not send any emails for any basis types having false values.
        basisList = new ArrayList<>();
        parentheses = new String();

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

//  @Scheduled(cron="0/12 * * * * ?") // executes every 12 seconds (for testing purposes)
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
        String reportType = basis + " Report";
        boolean isDaily = basis.equals(DAILY) ? true : false;
        LOG.info(String.format("Preparing %s...", reportType));
        DateMath dateMath = null;
        String requestBody = new String();
        Map<String, Integer> cspDataResults = new HashMap<>();
        Map<String, Integer> logstashResults = new HashMap<>();
        List<HitsItem> hitsItemList = new ArrayList<>();

        switch (basis) {
            case DAILY:     dateMath = ONE_DAY; break;
            case WEEKLY:    dateMath = ONE_WEEK; break;
            case MONTHLY:   dateMath = ONE_MONTH; break;
            case QUARTERLY: dateMath = THREE_MONTHS; break;
            case YEARLY:    dateMath = ONE_YEAR; break;
            default: break;
        }

        parentheses = getDates(basis);
        for (CspDataMappingType cdmt : CspDataMappingType.values()) {
            if (cdmt != CspDataMappingType.ALL) {  // "all" used for query construction only
                requestBody = requestBodyService.buildRequestBody(dateMath, NOW, cdmt);
                cspDataResults.put(cdmt.beautify(), elasticSearchClient.getNdocs(requestBody));
            }
        }
        for (LogstashMappingType lmt : LogstashMappingType.values()) {
            if (lmt != LogstashMappingType.ALL) {  // "all" used for query construction only
                requestBody = requestBodyService.buildRequestBody(dateMath, NOW, lmt);
                logstashResults.put(lmt.beautify() + " Logs", elasticSearchClient.getNlogs(requestBody));
            }
        }

        if (basis.equals(DAILY)) {
            requestBody = requestBodyService.buildRequestBodyForLogs(dateMath, NOW, LogstashMappingType.EXCEPTION);
            hitsItemList = elasticSearchClient.getLogData(requestBody);
        }

        Mail newMail = new Mail();
        newMail.setSenderName(mailFromName);
        newMail.setSenderEmail(mailFromMail);
        newMail.setSubject(reportType);
        newMail.setToArr(to);
        Map valuesMap = new HashMap();

        valuesMap.put("isDaily", isDaily);
        valuesMap.put("thymeleafTypeDescription", "DOCUMENT TYPE");
        valuesMap.put("thymeleafNumberDescription", "CREATED");
        valuesMap.put("recipient", "Administrator");
        valuesMap.put("signature", "Regular Reports Service");
        valuesMap.put("subject", reportType);
        valuesMap.put("message", String.format(msg, reportType, basis.getDescription(), parentheses));
        valuesMap.put("thymeleafMapA", cspDataResults);
        valuesMap.put("thymeleafMapB", logstashResults);
        if (isDaily) {
            valuesMap.put("excLogsList", hitsItemList);
            valuesMap.put("excLogsLimitSize", excLogsLimitSize);
            valuesMap.put("excLogsLimitMessage", String.format(excLogsLimitMessage, excLogsLimitSize, hitsItemList.size()));
        }

        newMail.setModel(valuesMap);

        try {
            regularReportsMailService.sendEmail(newMail);
        } catch (MessagingException e) {
            LOG.error(e.getLocalizedMessage());
        }

    }

    private String getDates(Basis basis) {
        String result = new String();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

        switch (basis) {

            case DAILY: {
                cal.add(Calendar.DATE, -1);
                Date yesterday = cal.getTime();
                result = sdf.format(yesterday);
                break;
            }

            case WEEKLY: {
                cal.add(Calendar.DATE, -1);
                Date dateEnded = cal.getTime();
                cal.add(Calendar.DATE, -7);
                Date dateStarted = cal.getTime();
                result = String.format("%s - %s", sdf.format(dateStarted), sdf.format(dateEnded));
                break;
            }

            case MONTHLY: {
                cal.add(Calendar.MONTH, -1);
                Date lastMonth = cal.getTime();
                sdf = new SimpleDateFormat("MMMM, yyyy");
                result = sdf.format(lastMonth);
                break;
            }

            case QUARTERLY: {
                Date date = new Date();
                sdf = new SimpleDateFormat("MMMM, ");
                for (int i = 1; i <= 3; i++) {
                    cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -i);
                    date = cal.getTime();
                    result += sdf.format(date);
                }
                result = result.substring(0, result.length()-2);
                break;
            }

            case YEARLY: {
                cal.add(Calendar.YEAR, -1);
                Date date = cal.getTime();
                sdf = new SimpleDateFormat("yyyy");
                result = sdf.format(date);
                break;
            }

            default: break;

        }

        return String.format("(%s)", result);
    }
}
