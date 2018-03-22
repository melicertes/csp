package com.intrasoft.csp.client;

import com.intrasoft.csp.regrep.commons.model.query.ElasticQuery;

public interface ElasticSearchClient {

    String DATA_INDEX = "cspdata";
    String LOGS_INDEX = "logstash*";

    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    // TODO Add date math enum to signature
    int getNdocsByType(CspDataMappingType type, String requestBody);

    int getNlogs(String requestBody);

    int getNlogs(ElasticQuery requestBody);


}
