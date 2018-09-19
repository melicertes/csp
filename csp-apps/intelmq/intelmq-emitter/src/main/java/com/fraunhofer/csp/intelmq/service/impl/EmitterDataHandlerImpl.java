package com.fraunhofer.csp.intelmq.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fraunhofer.csp.intelmq.service.EmitterDataHandler;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;

/**
 * Created by Majid Salehi on 20/04/2018
 */
@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler {

	private static final String EVENT_NODE = "Event";
	private static final String APPLICATION_ID = "intelmq";

	private static final Logger LOG = LoggerFactory.getLogger(EmitterDataHandlerImpl.class);

	@Value("${server.name}")
	String cspId;

	@Autowired
	CspClient cspClient;

	public String prettyPrintJsonString(JsonNode jsonNode) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(jsonNode.toString(), Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (Exception e) {
			return "Sorry, pretty print didn't work";
		}
	}

	@Override
	public ResponseEntity<String> handleIntelmqData(String intelmqEventData, String requestMethod) {
		LOG.debug("calling handleIntelmqData....");
		ResponseEntity<String> responseEntity;
		if (null == intelmqEventData) {
			LOG.error("Intelmq Event is null!");
			responseEntity = new ResponseEntity<>("FAILED TO READ EVENT FROM JSON NODE", HttpStatus.CONFLICT);
			return responseEntity;
		}

		LOG.debug("handleIntelmqData:intelmqEventData:json:" + intelmqEventData);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataObjectRaw = null;
		JsonNode dataObject = null;
		try {
			dataObjectRaw = mapper.readTree(intelmqEventData);
			LOG.info("Received from Intelmq emitter:" + dataObjectRaw.toString());
			JsonNode jsonNodeRaw = dataObjectRaw.get("raw");
			LOG.debug("handleIntelmqData:intelmqEventData:raw:" + prettyPrintJsonString(jsonNodeRaw));
			byte[] byteArray = Base64.decodeBase64(jsonNodeRaw.toString().getBytes());
			String decodedString = new String(byteArray);
			LOG.debug("intelmqEventData:decodedString:" + decodedString);
			//misp conform event converting
			dataObject = mapper.readTree(decodedString);
			ObjectNode objectNodeEvent = mapper.createObjectNode();
			objectNodeEvent.set(EVENT_NODE, dataObject);
			dataObject = objectNodeEvent;

		} catch (IOException ioex) {
			LOG.error("Event json object mapping failed.");
			// ioex.printStackTrace();
			responseEntity = new ResponseEntity<>("Event json object mapping failed with: " + ioex.getMessage(),
					HttpStatus.CONFLICT);
			return responseEntity;
		}

		DataParams dataParams = new DataParams();
		dataParams.setDateTime(new DateTime());

		dataParams.setOriginCspId(cspId);
		dataParams.setOriginApplicationId(APPLICATION_ID);
		dataParams.setCspId(cspId);
		dataParams.setApplicationId(APPLICATION_ID);

		String event_uuid = "";
		try {
			event_uuid = dataObject.get(EVENT_NODE).get("uuid").textValue();
		} catch (Exception ex) {
			// LOG.debug("Got no uuid node in dataobject Event node.");
			LOG.debug("Got uuid node in dataobject Event node. failed.");
		}
		LOG.debug("Received from IMQ emitter event with event_uuid: " + event_uuid);
		boolean intelmqObjextExists = false;
		if (event_uuid == null || event_uuid.isEmpty()) {
			UUID uuid = UUID.randomUUID();
			event_uuid = uuid.toString();
			LOG.debug("Create new uuid from IMQ emitter: " + uuid.toString());
			dataParams.setRecordId(event_uuid);
			dataParams.setOriginRecordId(event_uuid);
			ObjectNode eventNode = ((ObjectNode) dataObject.get(EVENT_NODE)).put("uuid", event_uuid);
			((ObjectNode) dataObject).set(EVENT_NODE, eventNode);
			LOG.debug("dataObject with uuid: " + dataObject);

		} else {
			intelmqObjextExists = true;
			dataParams.setRecordId(event_uuid);
			dataParams.setOriginRecordId(event_uuid);
		}
		LOG.info("Intelmq emitter event has uuid Object exists: " + intelmqObjextExists);
		/**
		 * TODO Issue setUrl how does the url update from emitter
		 */
		// dataParams.setUrl(protocol + "://" + uiHost + ":" + port + "/events/" +
		// uuid);
		String dataparamURL = dataObjectRaw.get("feed.url").toString();
		// String dataparamURL = "";
		LOG.debug("Integration data:dataParams:feed.url: " + dataparamURL);
		dataParams.setUrl(dataparamURL);

		IntegrationData integrationData = new IntegrationData();
		integrationData.setDataParams(dataParams);

		// set SharingParams
		SharingParams sharingParams = new SharingParams();
		sharingParams.setTcId(null);
		sharingParams.setTeamId(null);
		//setIsExternal(true) IL only sends to the prime app (MISP) and not to RT
		sharingParams.setIsExternal(true);
		sharingParams.setToShare(false);
		/**
		 * TODO Issue setToShare how does the setToShare from emitter
		 */
		/*
		 * try { Boolean eventPublished =
		 * Boolean.parseBoolean(dataObject.get(EVENT_NODE).get("published").toString());
		 * LOG.debug("eventPublished: " + eventPublished);
		 * sharingParams.setToShare(eventPublished); if
		 * (dataObject.get(EVENT_NODE).get("distribution").textValue().equals("0")) {
		 * sharingParams.setToShare(false);
		 * LOG.debug("Integration data setToShare false(distribution=0)"); } } catch
		 * (Exception e1) { sharingParams.setToShare(false);
		 * LOG.debug("Integration data setToShare false."); }
		 */

		integrationData.setSharingParams(sharingParams);
		integrationData.setDataObject(dataObject);

		IntegrationDataType integrationDataType = IntegrationDataType.EVENT;
		try {
			if (dataObject.get(EVENT_NODE).has("Tag")) {
				for (JsonNode jn : dataObject.get(EVENT_NODE).get("Tag")) {
					if (jn.get("name").textValue().equals("threat")) {
						integrationDataType = IntegrationDataType.THREAT;

					}
				}
			}
		} catch (Exception ex) {
			// LOG.error("Intelmq emitter cannot get Event->Tag node.");
			LOG.debug("Got Tag node in dataobject Event node. failed with: ", ex);
		}

		LOG.debug("Intelmq emitter we have IntegrationDataType:" + integrationDataType);
		integrationData.setDataType(integrationDataType);
		LOG.debug("Integration data: " + integrationData.toString());

		try {
			if (intelmqObjextExists) {
				LOG.debug("§§§§§§§§§§§§§§§§ INTELMQ EMITTER calling updateIntegrationData UPDATE UPDATE UPDATE UPDATE");
				responseEntity = cspClient.updateIntegrationData(integrationData);
			} else {
				LOG.debug("§§§§§§§§§§§§§§§§ INTELMQ EMITTER calling postIntegrationData POST POST POST POST");
				responseEntity = cspClient.postIntegrationData(integrationData);
			}
		} catch (Exception ex) {
			LOG.error("Forward to IL failed with: ", ex);
			responseEntity = new ResponseEntity<>(ex.getMessage(), HttpStatus.FAILED_DEPENDENCY);
		}
		return responseEntity;

	}

	@SuppressWarnings("unused")
	private void log2File(JsonNode dataObject, UUID uuid) {
		try (FileWriter file = new FileWriter(uuid + ".json")) {

			file.write(prettyPrintJsonString(dataObject));
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
