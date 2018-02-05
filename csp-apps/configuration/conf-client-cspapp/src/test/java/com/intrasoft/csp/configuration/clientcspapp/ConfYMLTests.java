package com.intrasoft.csp.configuration.clientcspapp;


import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.intrasoft.csp.conf.clientcspapp.ConfClientCspApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={ConfClientCspApplication.class},
        properties = {"spring.datasource.url=jdbc:h2:mem:testdata;DB_CLOSE_ON_EXIT=FALSE"})
@Slf4j
public class ConfYMLTests {

    @Autowired
    ApplicationContext context;

    @Test
    public void parseInputYML() throws Exception {

        final Resource resource = context.getResource("classpath:yml-testfile.yml");


        YamlReader r = new YamlReader(new InputStreamReader(resource.getInputStream()));

        Map data = (Map) r.read();

        r.close();

        log.info("yml : {}", data);

        Map apache = (Map) ((Map)data.get("services")).get("apache");
        List aliases = (List) ((Map) ((Map)((Map)apache.get("networks")).get("default"))).get("aliases");

        log.info("Aliases: {}", aliases);

        //clear
        aliases.clear();
        //set 2 new
        aliases.add("test.network1.local");
        aliases.add("test.network2.local");

        //save
        YamlWriter w = new YamlWriter(new FileWriter("testfile.yml"));
        w.write(data);
        w.close();



    }
}
