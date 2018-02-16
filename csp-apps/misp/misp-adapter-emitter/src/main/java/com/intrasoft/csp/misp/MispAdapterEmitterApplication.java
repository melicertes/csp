package com.intrasoft.csp.misp;

import com.intrasoft.csp.misp.service.EmitterSubscriber;
import com.intrasoft.csp.misp.service.impl.EmitterSubscriberImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MispAdapterEmitterApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(MispAdapterEmitterApplication.class, args);
		context.getBean(EmitterSubscriberImpl.class).subscribe();
	}
}
