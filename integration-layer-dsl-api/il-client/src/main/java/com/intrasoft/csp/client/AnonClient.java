package com.intrasoft.csp.client;


import com.intrasoft.csp.commons.model.IntegrationAnonData;

/**
 * Created by chris on 14/7/2017.
 */
public interface AnonClient {

    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    public IntegrationAnonData getAnonData(Object object);
}
