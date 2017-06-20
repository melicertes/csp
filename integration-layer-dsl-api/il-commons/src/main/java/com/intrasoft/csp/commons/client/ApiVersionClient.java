package com.intrasoft.csp.commons.client;


import com.intrasoft.csp.commons.model.VersionDTO;

/**
 * Created by akribopo on 28/2/2015.
 */
public interface ApiVersionClient {

    VersionDTO getApiVersion();
    
    String getContext();
    
    String getApiUrl();

    void updateContext();
}
