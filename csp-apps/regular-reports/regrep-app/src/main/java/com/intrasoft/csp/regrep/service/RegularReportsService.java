package com.intrasoft.csp.regrep.service;

public interface RegularReportsService {

    void reportDaily();
    void reportWeekly();
    void reportMonthly();
    void reportQuarterly();
    void reportYearly();
    void report(Basis basis);

}
