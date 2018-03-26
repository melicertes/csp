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
import com.fraunhofer.csp.intelmq.service.EmitterDataHandler;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;

@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler {

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
			LOG.error("IntelmqEvent is null!");
			responseEntity = new ResponseEntity<>("FAILED TO READ EVENT FROM JSON NODE", HttpStatus.CONFLICT);
			return responseEntity;
		}

		LOG.debug("intelmqEventData:json:" + intelmqEventData);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataObject = null;
		try {
			dataObject = mapper.readTree(intelmqEventData);
		} catch (IOException e1) {
			LOG.error("Event json object mapping failed with: ", e1.getMessage());
			e1.printStackTrace();
			responseEntity = new ResponseEntity<>("Event json object mapping failed with: " + e1.getMessage(),
					HttpStatus.CONFLICT);
			return responseEntity;
		}
		LOG.info("Received from Intelmq emmiter: " + dataObject.toString());

		JsonNode jsonNodeRaw = dataObject.get("raw");

		LOG.debug("intelmqEventData:raw" + prettyPrintJsonString(jsonNodeRaw));
		byte[] byteArray = Base64.decodeBase64(jsonNodeRaw.toString().getBytes());
		String decodedString = new String(byteArray);
		LOG.debug("intelmqEventData:decodedString" + decodedString);

		DataParams dataParams = new DataParams();
		dataParams.setDateTime(new DateTime());
		UUID uuid = UUID.randomUUID();
		dataParams.setOriginCspId(cspId);
		dataParams.setOriginApplicationId(APPLICATION_ID);
		dataParams.setOriginRecordId(uuid.toString());
		dataParams.setCspId(cspId);
		dataParams.setApplicationId(APPLICATION_ID);
		dataParams.setRecordId(uuid.toString());

		// TODO OK?
		String dataparamURL = dataObject.get("feed.url").toString();
		// String dataparamURL = "";
		LOG.debug("Integration data:dataParams:feed.url: " + dataparamURL);
		dataParams.setUrl(dataparamURL);

		IntegrationData integrationData = new IntegrationData();
		integrationData.setDataParams(dataParams);

		// set SharingParams
		SharingParams sharingParams = new SharingParams();
		sharingParams.setTcId(null);
		sharingParams.setTeamId(null);
		sharingParams.setIsExternal(false);
		// TODO OK?
		sharingParams.setToShare(true);
		LOG.debug("setToShare: " + true);

		integrationData.setSharingParams(sharingParams);
		// TODO OK?
		integrationData.setDataObject(dataObject);

		// if (LOG.isDebugEnabled())
		// log2File(dataObject, uuid);

		IntegrationDataType integrationDataType = IntegrationDataType.EVENT;

		integrationData.setDataType(integrationDataType);
		LOG.debug("Integration data: " + integrationData.toString());

		boolean intelmqObjextExists = false;

		LOG.debug("Intelmq Object exists: " + intelmqObjextExists);

		try {
			if (intelmqObjextExists) {
				LOG.debug("§§§§§§§§§§§§§§§§ INTELMQ EMITTER calling updateIntegrationData UPDATE UPDATE UPDATE UPDATE");
				responseEntity = cspClient.updateIntegrationData(integrationData);
			} else {
				LOG.debug("§§§§§§§§§§§§§§§§ INTELMQ EMITTER calling postIntegrationData POST POST POST POST");
				responseEntity = cspClient.postIntegrationData(integrationData);
			}
		} catch (Exception e) {
			LOG.error("Forward to IL failed with: ", e);

			responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
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
