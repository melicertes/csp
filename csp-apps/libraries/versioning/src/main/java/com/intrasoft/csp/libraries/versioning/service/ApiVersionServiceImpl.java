package com.intrasoft.csp.libraries.versioning.service;


import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiVersionServiceImpl implements ApiVersionService {
    /**
     * this is the API version we support
     */
    private VersionDTO version;

    @Autowired
    public ApiVersionServiceImpl(@Value("#{new com.intrasoft.csp.libraries.versioning.model.VersionDTO()}") VersionDTO version) {
        this.version = version;
    }

    @Override
    public VersionDTO getApiVersion() {
        return version;
    }

}
