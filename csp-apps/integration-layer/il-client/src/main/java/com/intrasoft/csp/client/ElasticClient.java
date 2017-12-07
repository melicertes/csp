package com.intrasoft.csp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.commons.model.IntegrationData;

import java.io.IOException;

public interface ElasticClient {
    public void setProtocolHostPort(String protocol, String host, String port);
    public String getContext();
    public boolean objectExists(IntegrationData integrationData) throws IOException;
    public JsonNode getESobject(IntegrationData integrationData) throws IOException;
    public JsonNode getESobjectFromOrigin(IntegrationData integrationData) throws IOException;
}
