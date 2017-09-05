package com.intrasoft.csp.server.policy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.policy.domain.exception.CouldNotDeleteException;
import com.intrasoft.csp.server.policy.domain.exception.PolicyNotFoundException;
import com.intrasoft.csp.server.policy.domain.exception.PolicySaveException;
import com.intrasoft.csp.server.policy.domain.model.EvaluatedPolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.utils.MockUtils;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class,MockUtils.class},
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
        })
@MockEndpointsAndSkip("http:*")
public class SharingPolicyServiceTest implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(SharingPolicyServiceTest.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TC)
    private MockEndpoint mockedTC;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+ECSP)
    private MockEndpoint mockedEcsp;

    @MockBean
    CamelRestService camelRestService;

    @SpyBean
    SharingPolicyService sharingPolicyService;

    @MockBean
    AnonClient anonClient;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringCamelContext springCamelContext;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Environment environment;

    DataParams anonObject;

    @Before
    public void init() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        mockUtils.setSpringCamelContext(springCamelContext);
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DSL),mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(DDL), mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX,routes.apply(ECSP), mockedEcsp.getEndpointUri());
        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class),anyObject()))
                .thenReturn(mockUtils.getAllMockedTrustCircles(3));
        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(TrustCircle.class)))
                .thenReturn(mockUtils.getMockedTrustCircle(3));
        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1,"http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(2,"http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(3,"http://external.csp%s.com"));
        IntegrationAnonData integrationAnonData = new IntegrationAnonData();

        anonObject = new DataParams();
        anonObject.setCspId("AnonymizedCspId");
        anonObject.setApplicationId("AnonymizedApplicationId");

        integrationAnonData.setDataObject(anonObject);
        Mockito.when(anonClient.postAnonData(anyObject())).thenReturn(integrationAnonData);
    }

    @DirtiesContext
    @Test
    public void evaluateTest(){
        EvaluatedPolicyDTO evaluatedPolicyDTO = sharingPolicyService.evaluate(IntegrationDataType.ARTEFACT);
        SharingPolicyAction action = evaluatedPolicyDTO.getSharingPolicyAction();
        assertThat(action, is(SharingPolicyAction.NO_ACTION_FOUND));
    }

    @DirtiesContext
    @Test
    public void dslFlow1TestUsingCamelEndpoint() throws Exception {
        EvaluatedPolicyDTO evaluatedPolicyDTO = new EvaluatedPolicyDTO();
        evaluatedPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        Mockito.when(sharingPolicyService.evaluate(eq(IntegrationDataType.INCIDENT))).thenReturn(evaluatedPolicyDTO);
        mockUtils.sendFlow1IntegrationData(mvc,false);

        mockedEcsp.expectedMessageCount(0); //The will be no condition to the aboved mocked call, so it will fallback to default action
        mockedEcsp.assertIsSatisfied();
        List<Exchange> list = mockedEcsp.getReceivedExchanges();
        int i=0;
        for (Exchange exchange : list) {
            i++;
            Message in = exchange.getIn();
            EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://external.csp"+i+".com"));
            String anonObjectJsonStrExpected = objectMapper.writeValueAsString(anonObject);
            String anonObjectJsonStrToCompare = objectMapper.writeValueAsString(enhancedTeamDTO.getIntegrationData().getDataObject());
            assertThat(anonObjectJsonStrToCompare,is(anonObjectJsonStrExpected));
        }
    }

    @DirtiesContext
    @Test
    public void evaluatePolicyTest() throws Exception {
        String condition = "function(i,t) i.getDataParams().getCspId() != null && t.getShortName() != null";
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.INCIDENT);
        newPolicyDTO.setCondition(condition);
        sharingPolicyService.savePolicy(newPolicyDTO);

        //ADD A NOT ACTIVE POLICY WITH HIGHEST PRIORITY to test it will not trigger
        newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(false);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_AS_IS);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.INCIDENT);
        newPolicyDTO.setCondition(condition);
        sharingPolicyService.savePolicy(newPolicyDTO);

        mockUtils.sendFlow1IntegrationData(mvc,false);

        mockedEcsp.expectedMessageCount(3);
        mockedEcsp.assertIsSatisfied();
        List<Exchange> list = mockedEcsp.getReceivedExchanges();
        int i=0;
        for (Exchange exchange : list) {
            i++;
            Message in = exchange.getIn();
            EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://external.csp"+i+".com"));
            String anonObjectJsonStrExpected = objectMapper.writeValueAsString(anonObject);
            String anonObjectJsonStrToCompare = objectMapper.writeValueAsString(enhancedTeamDTO.getIntegrationData().getDataObject());
            assertThat(anonObjectJsonStrToCompare,is(anonObjectJsonStrExpected));
        }
    }

    @DirtiesContext
    @Test
    public void evaluateDefaultPolicyWithEmptyConditionTest() throws Exception {
        String condition = "a condition that fails with exception and should fallback to default";
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.INCIDENT);
        newPolicyDTO.setCondition(condition);
        sharingPolicyService.savePolicy(newPolicyDTO);

        //ADD A NOT ACTIVE POLICY WITH HIGHEST PRIORITY to test it will not trigger
        newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(false);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_AS_IS);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.INCIDENT);
        newPolicyDTO.setCondition(condition);
        sharingPolicyService.savePolicy(newPolicyDTO);

        mockUtils.sendFlow1IntegrationData(mvc,false);

        mockedEcsp.expectedMessageCount(0);
        mockedEcsp.assertIsSatisfied();
        List<Exchange> list = mockedEcsp.getReceivedExchanges();
        int i=0;
        for (Exchange exchange : list) {
            i++;
            Message in = exchange.getIn();
            EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://external.csp"+i+".com"));
            String anonObjectJsonStrExpected = objectMapper.writeValueAsString(anonObject);
            String anonObjectJsonStrToCompare = objectMapper.writeValueAsString(enhancedTeamDTO.getIntegrationData().getDataObject());
            assertThat(anonObjectJsonStrToCompare,is(anonObjectJsonStrExpected));
        }
    }

    @DirtiesContext
    @Test
    public void evaluatePolicyDoNotShareTest() throws Exception {
        String condition = "function(i,t) i.getDataObject() == t.getCspId()";
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.DO_NOT_SHARE);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.INCIDENT);
        newPolicyDTO.setCondition(condition);
        sharingPolicyService.savePolicy(newPolicyDTO);

        mockUtils.sendFlow1IntegrationData(mvc,false);

        mockedEcsp.expectedMessageCount(0);
        mockedEcsp.assertIsSatisfied();
    }

    @DirtiesContext
    @Test
    public void checkConditionTest() throws ScriptException {
        String condition = "function(i,t) i.getDataObject() == t.getCspId()";

        PolicyDTO policyDTO = new PolicyDTO();
        policyDTO.setCondition(condition);

        Team team = new Team();
        team.setCspId("testCspId");

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataObject("testCspId");

        assertThat(sharingPolicyService.checkCondition(condition,integrationData,team), is(true));
    }

    @DirtiesContext
    @Test
    public void policyNotFoundExceptionTest(){
        Integer id = 123456;
        try {
            sharingPolicyService.getPolicyById(123456);
            fail("Expected PolicyNotFoundException");
        }catch (PolicyNotFoundException e){
            assertThat(e.getMessage(),containsString("Could not find a policy with this id: "+id));
        }
    }

    @DirtiesContext
    @Test
    public void policySaveExceptionTest(){
        try {
            PolicyDTO policyDTO = new PolicyDTO();
            sharingPolicyService.savePolicy(policyDTO);
            fail("Expected PolicySaveException");
        }catch (PolicySaveException e){
            assertThat(e.getMessage(),containsString("The sharing policy object you provided could not be saved. " +
                    "Check if passing empty or null fields"));
        }
    }

    @DirtiesContext
    @Test
    public void policyCouldNotDeleteExceptionTest(){
        Integer id = 12345678;
        try {
            sharingPolicyService.deletePolicy(id);
            fail("Expected CouldNotDeleteException");
        }catch (CouldNotDeleteException e){
            assertThat(e.getMessage(),containsString(String.format("Could not delete policy with this id: %d. Does it exist?",id)));
        }
    }

    @DirtiesContext
    @Test
    public void savePolicyAndGetByIdTest(){
        String condition = "test condition";
        //insert
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.CONTACT);
        newPolicyDTO.setCondition(condition);

        PolicyDTO savedDto = sharingPolicyService.savePolicy(newPolicyDTO);

        assertThat(savedDto.getActive(),is(true));
        assertThat(savedDto.getSharingPolicyAction(),is(SharingPolicyAction.SHARE_ANONYMIZED));
        assertThat(savedDto.getIntegrationDataType(),is(IntegrationDataType.CONTACT));
        assertThat(savedDto.getCondition(),is(condition));
        assertThat(savedDto.getId(),greaterThan(0));

        //update
        PolicyDTO saveDto = new PolicyDTO(savedDto.getId(),null, IntegrationDataType.ARTEFACT,
                condition+"upd",SharingPolicyAction.DO_NOT_SHARE);
        PolicyDTO updatedDto = sharingPolicyService.savePolicy(saveDto);
        assertNull(updatedDto.getActive());
        assertThat(updatedDto.getSharingPolicyAction(),is(SharingPolicyAction.DO_NOT_SHARE));
        assertThat(updatedDto.getIntegrationDataType(),is(IntegrationDataType.ARTEFACT));
        assertThat(updatedDto.getCondition(),is(condition+"upd"));
        assertThat(updatedDto.getId(),is(savedDto.getId()));

        PolicyDTO dto = sharingPolicyService.getPolicyById(savedDto.getId());
        assertNull(dto.getActive());
        assertThat(dto.getSharingPolicyAction(),is(SharingPolicyAction.DO_NOT_SHARE));
        assertThat(dto.getIntegrationDataType(),is(IntegrationDataType.ARTEFACT));
        assertThat(dto.getCondition(),is(condition+"upd"));
        assertThat(dto.getId(),is(savedDto.getId()));
    }

    @DirtiesContext
    @Test
    public void deletePolicyTest(){
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.CONTACT);
        newPolicyDTO.setCondition("condition");

        PolicyDTO savedDto = sharingPolicyService.savePolicy(newPolicyDTO);

        sharingPolicyService.deletePolicy(savedDto.getId());

        try {
            sharingPolicyService.getPolicyById(savedDto.getId());
            fail("Expected PolicyNotFoundException");
        }catch (PolicyNotFoundException e){
            assertThat(e.getMessage(),containsString("Could not find a policy with this id: "+savedDto.getId()));
        }
    }

    @DirtiesContext
    @Test
    public void getAllPoliciesTest(){
        List<PolicyDTO> list = sharingPolicyService.getPolicies();
        assertThat(list.size(),is(0));

        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.CONTACT);
        newPolicyDTO.setCondition("condition");
        sharingPolicyService.savePolicy(newPolicyDTO);

        list = sharingPolicyService.getPolicies();
        assertThat(list.size(),is(1));

        newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(false);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_AS_IS);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.ARTEFACT);
        newPolicyDTO.setCondition("condition2");
        sharingPolicyService.savePolicy(newPolicyDTO);

        list = sharingPolicyService.getPolicies();
        assertThat(list.size(),is(2));

        list = list.stream().sorted(Comparator.comparing(p->p.getId())).collect(Collectors.toList());

        assertThat(list.get(0).getActive(),is(true));
        assertThat(list.get(0).getSharingPolicyAction(),is(SharingPolicyAction.SHARE_ANONYMIZED));
        assertThat(list.get(0).getIntegrationDataType(),is(IntegrationDataType.CONTACT));
        assertThat(list.get(0).getCondition(),is("condition"));
        assertThat(list.get(0).getId(),is(1));

        assertThat(list.get(1).getActive(),is(false));
        assertThat(list.get(1).getSharingPolicyAction(),is(SharingPolicyAction.SHARE_AS_IS));
        assertThat(list.get(1).getIntegrationDataType(),is(IntegrationDataType.ARTEFACT));
        assertThat(list.get(1).getCondition(),is("condition2"));
        assertThat(list.get(1).getId(),is(2));


    }

    @DirtiesContext
    @Test
    public void getPolicyByActionTest(){
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.CONTACT);
        newPolicyDTO.setCondition("condition");
        sharingPolicyService.savePolicy(newPolicyDTO);

        newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(false);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_AS_IS);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.ARTEFACT);
        newPolicyDTO.setCondition("condition2");
        sharingPolicyService.savePolicy(newPolicyDTO);

        List<PolicyDTO> list = sharingPolicyService.getPoliciesByAction(SharingPolicyAction.SHARE_ANONYMIZED);
        assertThat(list.size(),is(1));

        assertThat(list.get(0).getActive(),is(true));
        assertThat(list.get(0).getSharingPolicyAction(),is(SharingPolicyAction.SHARE_ANONYMIZED));
        assertThat(list.get(0).getIntegrationDataType(),is(IntegrationDataType.CONTACT));
        assertThat(list.get(0).getCondition(),is("condition"));
        assertThat(list.get(0).getId(),is(1));
    }

    @DirtiesContext
    @Test
    public void getPolicyByDataTypeTest(){
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.CONTACT);
        newPolicyDTO.setCondition("condition");
        sharingPolicyService.savePolicy(newPolicyDTO);

        newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(false);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_AS_IS);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.ARTEFACT);
        newPolicyDTO.setCondition("condition2");
        sharingPolicyService.savePolicy(newPolicyDTO);

        List<PolicyDTO> list = sharingPolicyService.getPoliciesByDataType(IntegrationDataType.ARTEFACT);
        assertThat(list.size(),is(1));

        assertThat(list.get(0).getActive(),is(false));
        assertThat(list.get(0).getSharingPolicyAction(),is(SharingPolicyAction.SHARE_AS_IS));
        assertThat(list.get(0).getIntegrationDataType(),is(IntegrationDataType.ARTEFACT));
        assertThat(list.get(0).getCondition(),is("condition2"));
        assertThat(list.get(0).getId(),is(2));
    }

    @DirtiesContext
    @Test
    public void deleteAllPoliciesTest(){
        PolicyDTO newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(true);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_ANONYMIZED);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.CONTACT);
        newPolicyDTO.setCondition("condition");
        sharingPolicyService.savePolicy(newPolicyDTO);

        newPolicyDTO = new PolicyDTO();
        newPolicyDTO.setActive(false);
        newPolicyDTO.setSharingPolicyAction(SharingPolicyAction.SHARE_AS_IS);
        newPolicyDTO.setIntegrationDataType(IntegrationDataType.ARTEFACT);
        newPolicyDTO.setCondition("condition2");
        sharingPolicyService.savePolicy(newPolicyDTO);

        List<PolicyDTO> list = sharingPolicyService.getPolicies();
        assertThat(list.size(),is(2));
        sharingPolicyService.deleteAllPolicies();
        list = sharingPolicyService.getPolicies();
        assertThat(list.size(),is(0));

    }
}
