package com.intrasoft.csp.misp;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.intrasoft.csp.client","com.intrasoft.csp.libraries.versioning", "com.intrasoft.csp.commons.routes"})
public class Config {

    String GET_API_VERSION = "apiversion";
}
