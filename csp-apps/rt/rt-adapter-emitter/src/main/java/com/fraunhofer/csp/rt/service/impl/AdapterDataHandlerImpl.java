package com.fraunhofer.csp.rt.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.fraunhofer.csp.rt.app.IncidentCustomFields;
import com.fraunhofer.csp.rt.app.RtAppClient;
import com.fraunhofer.csp.rt.app.RtQueues;
import com.fraunhofer.csp.rt.domain.model.Origin;
import com.fraunhofer.csp.rt.domain.service.impl.OriginServiceImpl;
import com.fraunhofer.csp.rt.service.AdapterDataHandler;
import com.fraunhofer.csp.rt.ticket.CfSharing;
import com.fraunhofer.csp.rt.ticket.Ticket;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Service
public class AdapterDataHandlerImpl implements AdapterDataHandler {

	@Autowired
	OriginServiceImpl originService;

	@Value("${server.name}")
	String cspId;

	@Autowired
	RtAppClient rtAppClient;

	@Override
	public ResponseEntity<String> handleIntegrationData(IntegrationData integrationData, String requestMethod) {

		final Logger LOG = LoggerFactory.getLogger(AdapterDataHandlerImpl.class);
		LOG.debug("handleIntegrationData:" + requestMethod);
		LOG.debug("handleIntegrationData:" + integrationData);
		integrationData.getSharingParams().setToShare(false);
		// #issue 38
		// https://gitlab.fokus.fraunhofer.de/EU-CSP/csp-apps/issues/38
		// String originplusApplicationId = integrationData.getDataParams().getCspId() +
		// ":"
		// + integrationData.getDataParams().getOriginApplicationId();

		String originplusApplicationId = integrationData.getDataParams().getCspId();

		LOG.debug("originplusApplicationId: {}", originplusApplicationId);

		JsonNode jsonNode = null;
		jsonNode = new ObjectMapper().convertValue(integrationData.getDataObject(), JsonNode.class);
		// LOG.info(jsonNode.toString());
		try {
			LOG.debug(new ObjectMapper().writeValueAsString(integrationData.getDataObject()));
		} catch (JsonProcessingException e) {
			LOG.error("handleIntegrationData failed with:", e);
		}

		//LOG.debug("handleIntegrationData:jsonNode:" + jsonNode.toString());

		String uuid = null;
		String event_info = null;
		String threat_level_id = "3";
		Ticket incident = null;
		// we have a INCIDENT
		if (integrationData.getDataType() == IntegrationDataType.INCIDENT) {
			ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			try {
				incident = mapper.treeToValue(jsonNode, Ticket.class);
				//(new TicketPrinter()).print(incident);
				incident.setSharing(CfSharing.NO_SHARING.toString());
			} catch (IOException e1) {
				LOG.error("handleIntegrationData failed to getIncident from JSON Node:", e1);
				e1.printStackTrace();

				ResponseEntity<String> responseEntity = new ResponseEntity<>("FAILED TO READ INCIDENT FROM JSON NODE",
						HttpStatus.CONFLICT);
				return responseEntity;
			}

			uuid = incident.getCustomField(IncidentCustomFields.CF_RT_UUID);
		}
		// We have EVENT
		else {
			try {

				uuid = jsonNode.get("Event").get("uuid").toString().replace("\"", "");
				LOG.debug("Get event_uuid: " + uuid);
			} catch (Exception e) {
				LOG.warn("FAILED TO READ UUID FROM JSON EVENT NODE.");
				LOG.warn("READ UUID FROM JSON EVENT NODE. FAILED WITH:" + e);
			}
			try {
				event_info = jsonNode.get("Event").get("info").toString().replace("\"", "");
				LOG.debug("Get event_info: " + event_info);
			} catch (Exception e) {
				LOG.warn("FAILED TO READ INFO NODE.");
				LOG.warn("READ EVENT INFO NODE. FAILED WITH:" + e);
			}
			try {
				// "threat_level_id": "1",
				threat_level_id = jsonNode.get("Event").get("threat_level_id").toString().replace("\"", "");
				LOG.debug("Get threat_level_id: " + threat_level_id);
			} catch (Exception e) {
				LOG.warn("FAILED TO READ EVENT threat_level_id NODE.");
				LOG.warn("READ threat_level_id NODE. FAILED WITH:" + e);
			}
		}
		// WE have a intelmq event
		if (uuid == null || uuid.isEmpty()) {
			// TODO: workaround event from intelmq has no UUID so we create one
			/*
			 * LOG.error("handleIntegrationData failed to get uuid from JSON Node");
			 * 
			 * ResponseEntity<String> responseEntity = new
			 * ResponseEntity<>("FAILED TO READ UUID FROM JSON NODE", HttpStatus.CONFLICT);
			 * return responseEntity;
			 */
			LOG.debug("Create new UUID for event.");
			uuid = UUID.randomUUID().toString();

			try {
				String raw = jsonNode.get("raw").toString();
				byte[] decoded = Base64.decodeBase64(raw);
				event_info = new String(decoded, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.error("Decode raw string failed with:" + e);
			} catch (NullPointerException ex) {
				LOG.debug("Node has no raw information.");
			}
		}

		List<Origin> origins = originService.findByOriginRecordId(uuid);
		if (origins.isEmpty()) {
			LOG.debug("Entry for " + uuid + " not found.");
			Origin origin = new Origin();
			origin.setOriginCspId(integrationData.getDataParams().getOriginCspId());
			origin.setOriginApplicationId(integrationData.getDataParams().getOriginApplicationId());
			origin.setOriginRecordId(integrationData.getDataParams().getOriginRecordId());
			origin.setCspId(cspId);
			origin.setApplicationId("rt");
			origin.setRecordId(integrationData.getDataParams().getOriginRecordId());
			Origin org = originService.saveOrUpdate(origin);
			LOG.debug("Origin inserted: " + org);
		} else {
			LOG.debug("Origin params already found in table");
		}

		if (requestMethod.equals("DELETE")) {
			// TODO id is not unique among the csps,needs care and proderm
			// TODO Majid rtAppClient.deleteRtTicket(
		} else {
			String response = null;
			// we have an INCIDENT
			if (integrationData.getDataType() == IntegrationDataType.INCIDENT) {
				String ticketid = rtAppClient.getTicketIdForUUID(incident);
				if (ticketid == null) {
					LOG.info("############## ADAPTER CALLING addRtTicket #############");
					response = rtAppClient.addRtTicket(incident, originplusApplicationId,
							RtQueues.INCIDENT_QUEUE.toString());
					LOG.debug(response);
				} else {
					LOG.info("############## ADAPTER CALLING updateRtTicket #############");
					response = rtAppClient.updateRtTicket(incident, ticketid, originplusApplicationId);
					LOG.debug(response);
				}
				// we have a EVENT/THREAT
			} else {
				if (threat_level_id.equalsIgnoreCase("1")) {
					if (origins.isEmpty()) {
						LOG.info("############## ADAPTER CALLING addRtReport #############");
						response = rtAppClient.addRtReport(uuid, event_info, originplusApplicationId,
								integrationData.getDataParams().getUrl(), integrationData.getDataType());
					} else {
						LOG.info("############## ADAPTER CALLING updateRtReport #############");
						response = rtAppClient.updateRtReport(jsonNode, originplusApplicationId,
								integrationData.getDataParams().getUrl(), integrationData.getDataType());
					}
				} else
					LOG.debug("threat_level_id is NOT high, creating an event report is not necessary:"
							+ threat_level_id);

			}

		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
