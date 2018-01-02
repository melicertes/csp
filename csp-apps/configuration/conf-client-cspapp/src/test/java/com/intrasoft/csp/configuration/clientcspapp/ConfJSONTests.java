package com.intrasoft.csp.configuration.clientcspapp;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.clientcspapp.ConfClientCspApplication;
import com.intrasoft.csp.conf.clientcspapp.model.json.Environment;
import com.intrasoft.csp.conf.clientcspapp.model.json.Manifest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={ConfClientCspApplication.class}, properties = {"spring.datasource.url=jdbc:h2:mem:testdata;DB_CLOSE_ON_EXIT=FALSE"})
@Slf4j
public class ConfJSONTests {

    @Autowired
    ApplicationContext context;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    public void context() {}

    @Test
    public void parseInputJson() throws Exception {

        final Resource resource = context.getResource("classpath:env-fullExample.json");


        final Environment environment = jacksonObjectMapper.readValue(resource.getFile(), Environment.class);

        Assert.notNull(environment,"object must not be null");
        log.info("Parsed JSON as: {}", environment);

        String json = jacksonObjectMapper.writeValueAsString(environment);
        Assert.notNull(json,"object must not be null");

        log.info("JSON written: {}",json);

    }

    @Test
    public void parseInputRTJson() throws Exception {

        final Resource resource = context.getResource("classpath:env-basic-rt.json");
        final Environment environment = jacksonObjectMapper.readValue(resource.getFile(), Environment.class);

        Assert.notNull(environment,"object must not be null");
        log.info("Parsed RT JSON as: {}", environment);

        String json = jacksonObjectMapper.writeValueAsString(environment);
        Assert.notNull(json,"object must not be null");

        log.info("RT JSON written: {}",json);

    }

    @Test
    public void parseManifest10Json() throws  Exception {
        final Resource resource = context.getResource("classpath:manifest1_0.json");
        final Manifest manifest = jacksonObjectMapper.readValue(resource.getFile(), Manifest.class);

        Assert.notNull(manifest,"object must not be null");
        log.info("Parsed 1.0 JSON as: {}", manifest);

        Assert.isTrue(manifest.getFormat() == 1.0, "format must be 1.0");

        String json = jacksonObjectMapper.writeValueAsString(manifest);
        Assert.notNull(json,"object must not be null");

        log.info("1.0 JSON written: {}",json);
    }
    @Test
    public void parseManifest11Json() throws  Exception {
        final Resource resource = context.getResource("classpath:manifest1_1.json");
        final Manifest manifest = jacksonObjectMapper.readValue(resource.getFile(), Manifest.class);

        Assert.notNull(manifest,"object must not be null");
        log.info("Parsed 1.1 JSON as: {}", manifest);

        Assert.isTrue(manifest.getFormat() == 1.1, "format must be 1.1");

        String json = jacksonObjectMapper.writeValueAsString(manifest);
        Assert.notNull(json,"object must not be null");

        log.info("1.1 JSON written: {}",json);
    }
}
