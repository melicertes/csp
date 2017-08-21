package com.intrasoft.csp.libraries.versioning.client;

import com.intrasoft.csp.libraries.versioning.model.VersionDTO;

public interface ApiVersionClient {

    VersionDTO getApiVersion();
    
    String getContext();
    
    String getApiUrl();

    void updateContext();
}
