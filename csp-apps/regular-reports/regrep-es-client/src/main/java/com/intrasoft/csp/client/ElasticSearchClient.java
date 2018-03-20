package com.intrasoft.csp.client;

public interface ElasticSearchClient {

    String DATA_INDEX = "cspdata";

    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    // TODO Add date math enum to signature
    int getNdocsByType(CspDataMappingType type, String requestBody);


}
