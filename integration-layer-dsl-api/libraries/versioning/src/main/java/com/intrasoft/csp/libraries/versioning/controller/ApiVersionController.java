package com.intrasoft.csp.libraries.versioning.controller;

import com.intrasoft.csp.libraries.versioning.model.ContextUrl;
import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import com.intrasoft.csp.libraries.versioning.service.ApiVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiVersionController implements ContextUrl {

    private Logger LOG = LoggerFactory.getLogger(ApiVersionController.class);

    @Autowired
    ApiVersionService apiVersionService;

    @RequestMapping(value = GET_API_VERSION, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE )
    public VersionDTO getApiVersion() {
        VersionDTO versionDTO = apiVersionService.getApiVersion();
        LOG.trace("REST: Called {}, will return {}", GET_API_VERSION, versionDTO);
        return versionDTO;
    }
}