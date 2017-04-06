package com.sastix.csp.integration;

import com.sastix.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.routes.ContextUrl;
import com.sastix.csp.server.IntegrationLayerDslApiApplication;
import com.sastix.csp.server.processors.RecipientsProcessor;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.spi.UnitOfWork;
import org.apache.camel.spring.SpringCamelContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import org.springframework.boot.test.json.*;
import org.springframework.web.context.WebApplicationContext;

//import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by iskitsas on 4/3/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IntegrationLayerDslApiApplication.class)
//@ActiveProfiles("test")
public class IntegrationServerTest {

    private MockMvc mvc;

    @Spy
    RecipientsProcessor recipientsProcessor;

    @Autowired
    private WebApplicationContext webApplicationContext;



    @Before
    public void init() throws Exception {
        mvc=  webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
//        Mockito.doCallRealMethod().when(recipientsProcessor).process(exchange);
//        Mockito.doCallRealMethod().when(recipientsProcessor.process();
//        Mockito.spy(recipientsProcessor.process(exchange));
        Mockito.doNothing().when(recipientsProcessor).process(any());
//        Mockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//                return null;
//            }
//        }).when(Mockito.spy(recipientsProcessor)).process(any());
        //to capture args
        //final ArgumentCaptor<Exchange> captor = ArgumentCaptor.forClass(Exchange.class);
        //Mockito.doNothing().when(Mockito.spy(recipientsProcessor)).process(captor.capture());
    }

    @Test
    public void dslIntegrationDataTest() throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);
        mvc.perform(post(ContextUrl.DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(TestUtil.convertObjectToJsonBytes(integrationData))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
    }

    @Test
    public void dslIntegrationNoDataTypeTest() throws Exception {
        IntegrationData integrationData = new IntegrationData();
        mvc.perform(post(ContextUrl.DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(TestUtil.convertObjectToJsonBytes(integrationData))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()));
    }


}
