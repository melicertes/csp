package com.intrasoft.csp.vcb.admin;

import com.intrasoft.csp.vcb.admin.config.OpenfireProperties;
import com.intrasoft.csp.vcb.admin.config.VcbadminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
//@ComponentScan("com.intrasoft.csp.vcb.admin")
@EnableConfigurationProperties({ OpenfireProperties.class, VcbadminProperties.class })
//@EntityScan(basePackageClasses = { VcbAdminApplication.class, Jsr310JpaConverters.class })
@EntityScan("com.intrasoft.csp.vcb.commons.*")
@EnableScheduling
@EnableAsync
public class VcbAdminApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(VcbAdminApplication.class, args);
	}

}
