package com.intrasoft.csp.regrep;

import com.intrasoft.csp.regrep.commons.model.HitsItem;

import java.util.List;

public interface ElasticSearchClient {

    String DATA_INDEX = "cspdata";
    String LOGS_INDEX = "logstash*";

    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    int getNlogs(String requestBody);

    int getNdocs(String requestBody);

    // returns null on error
    List<HitsItem> getLogData(String requestBody);


}
