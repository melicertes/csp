package com.intrasoft.csp.client;

public interface ElasticSearchClient {

    String DATA_INDEX = "cspdata";
    String LOGS_INDEX = "logstash*";

    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    int getNlogs(String requestBody);

    int getNdocs(String requestBody);

}
