package com.intrasoft.csp.misp.adapter.config;

import com.intrasoft.csp.libraries.versioning.model.VersionDTO;
import com.intrasoft.csp.libraries.versioning.service.ApiVersionService;
import com.intrasoft.csp.libraries.versioning.service.ApiVersionServiceImpl;
import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Version Configuration.
 */
@Configuration
@ComponentScan({"com.intrasoft.csp.commons","com.intrasoft.csp.libraries"})
public class VersionConfiguration implements ApiContextUrl {

    public static VersionDTO MISP_SERVER_VERSION = new VersionDTO()
            .withMinVersion(Double.valueOf(REST_API_V1))
            .withMaxVersion(Double.valueOf(REST_API_V1))
            .withVersionContext(Double.valueOf(REST_API_V1),  API_BASE + "/v" + REST_API_V1);

    @Bean
    public ApiVersionService apiVersionService() {
            /*
             * you need to configure the api version service with the
			 * constructor argument of the api ranges you support
			 */
        return new ApiVersionServiceImpl(MISP_SERVER_VERSION);
    }
}
