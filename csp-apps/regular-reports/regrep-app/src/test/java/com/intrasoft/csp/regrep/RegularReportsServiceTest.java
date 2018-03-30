package com.intrasoft.csp.regrep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.regrep.impl.ElasticSearchClientImpl;
import com.intrasoft.csp.regrep.service.RegularReportsService;
import com.intrasoft.csp.regrep.service.impl.RegularReportsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {RegularReportsServiceImpl.class, ElasticSearchClientImpl.class, RestTemplate.class, ObjectMapper.class})
public class RegularReportsServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(RegularReportsService.class);

    @Autowired
    RegularReportsService regularReportsService;

    @Test
    public void firstTest() {

    }

}
