package com.fraunhofer.csp.rt.api;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.fraunhofer.csp.rt.app.RtAppClient;
import com.fraunhofer.csp.rt.app.RtQueues;
import com.fraunhofer.csp.rt.service.AdapterDataHandler;
import com.fraunhofer.csp.rt.ticket.CfSharing;
import com.fraunhofer.csp.rt.ticket.Ticket;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@RestController
public class RtAdapterController {

	private static final String RT_V1_ADAPTER = "/rt/v1/adapter";

	private static final Logger LOG = LoggerFactory.getLogger(RtAdapterController.class);

	@Autowired
	AdapterDataHandler adapterDataHandler;

	@Autowired
	RtAppClient rtAppClient;

	@RequestMapping(value = RT_V1_ADAPTER, consumes = { "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<String> synchNewIntData(@RequestBody IntegrationData integrationData) {
		LOG.info("RT Endpoint: POST received");
		return adapterDataHandler.handleIntegrationData(integrationData, "POST");
	}

	@RequestMapping(value = RT_V1_ADAPTER, consumes = { "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<String> synchUpdatedIntData(@RequestBody IntegrationData integrationData) {
		LOG.info("RT Endpoint: PUT received");
		return adapterDataHandler.handleIntegrationData(integrationData, "PUT");
	}

	@RequestMapping(value = RT_V1_ADAPTER, consumes = { "application/json" }, method = RequestMethod.DELETE)
	public ResponseEntity<String> synchDeletedIntData(@RequestBody IntegrationData integrationData) {
		LOG.info("RT Endpoint: DELETE received");
		return adapterDataHandler.handleIntegrationData(integrationData, "DELETE");
	}

	// just run post
	@RequestMapping(value = "/rt/adapter/test/{ticketid}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<?> putdoAllTestTicketById(@PathVariable String ticketid) {
		LOG.debug("################putdoAllTestTicketById BEGIN #####################");
		LOG.info("RT Endpoint:putdoAllTestTicketById GET TEST received.");
		Ticket incident = rtAppClient.getTicket(ticketid);
		LOG.debug("putdoAllTestTicketById:working with ticket id:" + incident.getId());

		IntegrationData integrationData = createIdataFromIncident(incident, IntegrationDataType.INCIDENT, "rt");

		adapterDataHandler.handleIntegrationData(integrationData, "POST");

		return adapterDataHandler.handleIntegrationData(integrationData, "PUT");

	}

	// just run a lot of tests
	@RequestMapping(value = "/rt/adapter/alltest/{ticketid}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<?> doAllTestTicketById(@PathVariable String ticketid) {
		LOG.debug("################doAllTestTicketById BEGIN #####################");
		LOG.info("RT Endpoint:doAllTestTicketById GET TEST received:" + ticketid);

		String message = rtAppClient.getMessage(ticketid);
		LOG.debug("doAllTestTicketById:got message:" + message);
		List<String> comments = rtAppClient.getComments(ticketid);
		if (comments != null && comments.size() > 0) {
			LOG.debug("doAllTestTicketById:got comments:" + comments.size());
			for (String comment : comments) {
				LOG.debug("doAllTestTicketById:got comment:" + comment);
			}
		}
		String response = rtAppClient.callRtNewReportTest();
		LOG.debug("doAllTestTicketById:callRtNewReportTest:" + response);

		Ticket incident = rtAppClient.getTicket(ticketid);
		if (incident != null)
			LOG.debug("doAllTestTicketById:working with ticket id:" + incident.getId());
		else {
			LOG.error("doAllTestTicketById:got null for id:" + ticketid);
			return new ResponseEntity<>("doAllTestTicketById done", HttpStatus.CONFLICT);
		}

		String uuid = rtAppClient.getTicketIdForUUID(incident);
		LOG.debug("doAllTestTicketById:working with ticket uuid:" + uuid);

		String Content = rtAppClient.testbuildAndAdjustLinks(incident);
		LOG.debug("doAllTestTicketById:testbuildAndAdjustLinks Content:" + Content);

		response = rtAppClient.callRtClientTicketsTest(ticketid);
		LOG.debug("doAllTestTicketById:callRtClientTicketsTest:" + response);

		incident.setUUID(UUID.randomUUID().toString());
		incident.setSubject("test subject from doAllTest:" + new DateTime());
		response = rtAppClient.addRtTicket(incident, "demo1-csp:rt", RtQueues.INCIDENT_QUEUE.toString());

		rtAppClient.addComment2Incident(response, "test message:" + new DateTime());
		List<String> mycomments = Arrays.asList("Hello", "World!", "How", "Are", "You");
		rtAppClient.updateComments2Incident(response, mycomments);

		ResponseEntity<String> responseEntity = new ResponseEntity<>("doAllTestTicketById done", HttpStatus.OK);
		LOG.debug("################doAllTestTicketById END#####################");
		return responseEntity;
	}

	// just for test
	private IntegrationData createIdataFromIncident(Ticket incident, IntegrationDataType itype, String applicationId) {
		LOG.debug("createIdataFromIncident:applicationId:" + applicationId);
		DataParams dataParams = new DataParams();
		dataParams.setCspId("demo1-csp");
		dataParams.setApplicationId(applicationId);
		String uuid = UUID.randomUUID().toString();
		dataParams.setRecordId(uuid);
		dataParams.setDateTime(new DateTime());
		dataParams.setOriginCspId("demo1-csp");
		dataParams.setOriginApplicationId(applicationId);
		dataParams.setOriginRecordId(uuid);
		String dataparamURL = "http://" + "localhost" + ":" + "80" + "/RTIR/Display.html?id=1";
		LOG.debug("Integration data:dataParams:url: " + dataparamURL);
		dataParams.setUrl(dataparamURL);
		SharingParams sharingParams = new SharingParams();
		sharingParams.setIsExternal(false);
		sharingParams.setToShare(true);
		IntegrationData integrationData = new IntegrationData();
		integrationData.setDataParams(dataParams);
		integrationData.setSharingParams(sharingParams);
		incident.setUUID(uuid);
		incident.setSubject("TEST Incident:" + new DateTime());
		incident.setSharing(CfSharing.DEFAULT_SHARING.toString());
		JsonNode incidentjsonNode = getJsonNodeFromIncident(incident);
		integrationData.setDataObject(incidentjsonNode);
		integrationData.setDataType(itype);

		LOG.debug("Integration data: " + integrationData.toString());
		return integrationData;
	}

	private JsonNode getJsonNodeFromIncident(Ticket incident) {
		// String ticketJsonInString = null;
		JsonNode incidentjsonNode = null;
		try {
			ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			// ticketJsonInString = mapper.writeValueAsString(incident);
			incidentjsonNode = mapper.convertValue(incident, JsonNode.class);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("createIdataFromIncident:" + e.getMessage());
		}
		return incidentjsonNode;
	}
}
