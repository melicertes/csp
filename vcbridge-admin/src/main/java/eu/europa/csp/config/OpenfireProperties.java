package eu.europa.csp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("openfire")
public class OpenfireProperties {

    /**
     * Configuration for openfire server
     */
    private String test = "mpla";

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

}
