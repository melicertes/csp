package com.intrasoft.csp.libraries.versioning.client;


import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import com.intrasoft.csp.libraries.versioning.exceptions.VersionNotSupportedException;
import com.intrasoft.csp.libraries.versioning.model.ContextUrl;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class ApiVersionClientImpl implements ApiVersionClient, ContextUrl {

    private Logger LOG = (Logger) LoggerFactory.getLogger(ApiVersionClient.class);

    private String protocol;
    private String host;
    private String port;
    private String context = null;
    private String apiVersion;
    private int apiUrlCounter = 0;
    private boolean lazyUpdate = true;

    @Autowired
    Environment env;

    RetryRestTemplate retryRestTemplate;

    public ApiVersionClientImpl(final String protocol,final String host, final String port, final String apiVersion, final RetryRestTemplate retryRestTemplate) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.retryRestTemplate = retryRestTemplate;
    }

    /**
     * For testing/mocking only usage
     */
    public ApiVersionClientImpl(final String protocol, final String host, final String port, final String apiVersion, final VersionDTO versionDTO, final RetryRestTemplate retryRestTemplate) {
        //disable lazyness when mocking
        lazyUpdate = false;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.retryRestTemplate = retryRestTemplate;
        this.apiVersion = apiVersion;
        if (!versionDTO.getVersionContexts().containsKey(Double.valueOf(apiVersion).toString())) {
            if (versionDTO.getMinVersion() > Double.valueOf(apiVersion)) {
                throw new VersionNotSupportedException("API Version " + apiVersion + " is outdated. Supported Versions are " + versionDTO.toString());
            } else {
                throw new VersionNotSupportedException("API Version " + apiVersion + " is not supported. Supported Versions are " + versionDTO.toString());
            }
        } else {
            context = versionDTO.getVersionContexts().get(Double.valueOf(apiVersion).toString());
        }
    }

    @Override
    public VersionDTO getApiVersion() {
        String url = getUrlRoot() + "/" + GET_API_VERSION;
        LOG.trace("API call: " + url);
        VersionDTO versionDTO = retryRestTemplate.getForObject(url, VersionDTO.class);
        LOG.trace("Response: " + versionDTO.toString());
        return versionDTO;
    }

    @Override
    public String getContext() {
        return context;
    }

    private String getUrlRoot() {
        return protocol+"://" + host + ":" + port;
    }

    public String getApiUrl() {
        if(lazyUpdate){ // the only case for now lazeUpdate is false, is when mocking VersionDTO
            if (apiUrlCounter % 200 == 0) { //true on first time (when apiUrlCounter=0)
                updateContext();
                if(apiUrlCounter > 0){
                    apiUrlCounter = 0; //reset counter to avoid overflow
                }
            }
            apiUrlCounter++;
        }
        return getUrlRoot() + context;
    }

    @Override
    public void updateContext() {
        final VersionDTO versionDTO = getApiVersion();
        context = versionDTO.getVersionContexts().get(Double.valueOf(apiVersion).toString());
    }

    public boolean isLazyUpdate() {
        return lazyUpdate;
    }

    public void setLazyUpdate(boolean lazyUpdate) {
        this.lazyUpdate = lazyUpdate;
    }
}
