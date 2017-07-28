package com.intrasoft.csp.ccs.server.config;

import com.intrasoft.csp.ccs.commons.routes.ApiContextUrl;
import com.intrasoft.csp.ccs.commons.model.VersionDTO;

import com.intrasoft.csp.ccs.commons.service.ApiVersionService;
import com.intrasoft.csp.ccs.commons.service.ApiVersionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Version Configuration.
 */
@Configuration
@ComponentScan("com.intrasoft.csp.ccs.commons")
public class VersionConfiguration implements ApiContextUrl {

    public static VersionDTO CCS_SERVER_VERSION = new VersionDTO()
            .withMinVersion(Double.valueOf(API_V1))
            .withMaxVersion(Double.valueOf(API_V1))
            .withVersionContext(Double.valueOf(API_V1),  "/v" + API_V1);

    @Bean
    public ApiVersionService apiVersionService() {
            /*
             * you need to configure the api version service with the
			 * constructor argument of the api ranges you support
			 */
        return new ApiVersionServiceImpl(CCS_SERVER_VERSION);
    }
}
