package com.intrasoft.csp.client;

import com.intrasoft.csp.commons.model.IntegrationData;

import java.io.IOException;

public interface ElasticClient {
    public void setProtocolHostPort(String protocol, String host, String port);
    public String getContext();
    public void searchObject(IntegrationData integrationData) throws IOException;
}
