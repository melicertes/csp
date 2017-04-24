package eu.europa.csp.vcbadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vcbadmin")
public class VcbadminProperties {

	Integer maxTaskRetries = 3;

	public Integer getMaxTaskRetries() {
		return maxTaskRetries;
	}

	public void setMaxTaskRetries(Integer maxTaskRetries) {
		this.maxTaskRetries = maxTaskRetries;
	}

}
