package com.intrasoft.csp.vcb.admin;

import com.intrasoft.csp.vcb.admin.config.TeleconfProperties;
import com.intrasoft.csp.vcb.admin.config.VcbadminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
//@ComponentScan("com.intrasoft.csp.vcb.admin")
@EnableConfigurationProperties({ TeleconfProperties.class, VcbadminProperties.class })
//@EntityScan(basePackageClasses = { VcbAdminApplication.class, Jsr310JpaConverters.class })
@EntityScan("com.intrasoft.csp.vcb.commons.*")
@EnableScheduling
@EnableAsync
public class VcbAdminApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) throws Throwable {
		SpringApplication.run(VcbAdminApplication.class, args);
	}

}
