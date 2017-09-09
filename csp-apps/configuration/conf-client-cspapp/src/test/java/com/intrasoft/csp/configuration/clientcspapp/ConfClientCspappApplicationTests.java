package com.intrasoft.csp.configuration.clientcspapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.clientcspapp.ConfClientCspApplication;
import com.intrasoft.csp.conf.clientcspapp.model.InstallationState;
import com.intrasoft.csp.conf.clientcspapp.model.SystemInstallationState;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemInstallationStateRepository;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={ConfClientCspApplication.class}, properties = {"spring.datasource.url=jdbc:h2:mem:testdata;DB_CLOSE_ON_EXIT=FALSE"})
@Slf4j
public class ConfClientCspappApplicationTests {

	private final String testUUID = "2513b89e-e073-4dc7-a43e-29a1ecce5a41";

	@Autowired
	SystemInstallationStateRepository repo;

	@Test
	public void contextLoads() {
	}


	@Test
	@Transactional
	public void testDb() {
		SystemInstallationState state = new SystemInstallationState(testUUID, InstallationState.NOT_STARTED, new RegistrationDTO());
		SystemInstallationState stateSaved = repo.save(state);
		Assert.assertNotNull("Id should be allocated", stateSaved.getId());
		Assert.assertEquals("UUID should be same", stateSaved.getCspId(), testUUID);

		log.info("Saved entity ID {}", stateSaved.getId());
	}

	@Test
	@Transactional
	public void testMarshalling() throws Exception {

		String json = IOUtils.toString(this.getClass().getResourceAsStream("/register/register-valid-1.json"), "UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		RegistrationDTO registration = mapper.readValue(json, RegistrationDTO.class);

		SystemInstallationState state = new SystemInstallationState(testUUID, InstallationState.NOT_STARTED, registration);

		SystemInstallationState saved = repo.save(state);

		saved = repo.findOne(saved.getId());

		Assert.assertNotNull("Id should be allocated", saved.getId());
		Assert.assertEquals("UUID should be same", saved.getCspId(), testUUID);

		Assert.assertEquals("registration object should be equal" , registration, saved.getCspRegistration());

		Assert.assertEquals("installation state should be NOT_STARTED", InstallationState.NOT_STARTED, saved.getInstallationState());
		log.info("Retrieved registration as {}",saved.getCspRegistration());

	}

}
