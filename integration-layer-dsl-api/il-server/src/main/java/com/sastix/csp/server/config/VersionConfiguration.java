package com.sastix.csp.server.config;

import com.sastix.csp.commons.model.VersionDTO;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.commons.service.ApiVersionService;
import com.sastix.csp.commons.service.ApiVersionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Version Configuration.
 */
@Configuration
@ComponentScan("com.sastix.csp.commons")
public class VersionConfiguration implements ContextUrl {

    public static VersionDTO CSP_SERVER_VERSION = new VersionDTO()
            .withMinVersion(Double.valueOf(REST_API_V1))
            .withMaxVersion(Double.valueOf(REST_API_V1))
            .withVersionContext(Double.valueOf(REST_API_V1),  "/v" + REST_API_V1);

    @Bean
    public ApiVersionService apiVersionService() {
            /*
             * you need to configure the api version service with the
			 * constructor argument of the api ranges you support
			 */
        return new ApiVersionServiceImpl(CSP_SERVER_VERSION);
    }
}
