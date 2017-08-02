package com.intrasoft.csp.integration.sandbox.client.csp;

import com.intrasoft.csp.libraries.versioning.controller.ApiVersionController;
import com.intrasoft.csp.server.CspApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by iskitsas on 5/3/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CspApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SmokeTest {
    @Autowired
    private ApiVersionController controller;

    @LocalServerPort
    private int port;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
