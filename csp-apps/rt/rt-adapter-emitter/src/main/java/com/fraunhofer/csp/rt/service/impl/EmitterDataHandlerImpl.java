
package com.fraunhofer.csp.rt.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.fraunhofer.csp.rt.app.IncidentCustomFields;
import com.fraunhofer.csp.rt.app.RtAppClient;
import com.fraunhofer.csp.rt.domain.model.Origin;
import com.fraunhofer.csp.rt.domain.model.RTuuid;
import com.fraunhofer.csp.rt.domain.service.impl.OriginServiceImpl;
import com.fraunhofer.csp.rt.domain.service.impl.RTuuidServiceImpl;
import com.fraunhofer.csp.rt.service.EmitterDataHandler;
import com.fraunhofer.csp.rt.ticket.CfSharing;
import com.fraunhofer.csp.rt.ticket.Ticket;
import com.intrasoft.csp.client.CspClient;
import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EmitterDataHandlerImpl.class);

	private static final String APPLICATION_ID = "rt";

	@Value("${rt.app.host}")
	String host;

	@Value("${rt.app.port}")
	String port;

	@Value("${rt.app.protocol}")
	String protocol;

	@Value("${server.name}")
	String cspId;

	@Autowired
	CspClient cspClient;

	@Autowired
	TrustCirclesClient tcClient;

	@Autowired
	OriginServiceImpl originService;

	@Autowired
	RTuuidServiceImpl rtUuidService;

	@Autowired
	RtAppClient rtAppClient;

	@Override
	public void handleRtData(String ticketid, boolean isDelete) {
		LOG.debug("handleRtData for ticket with id {}", ticketid);
		emittTicketData(ticketid, isDelete);
	}

	@Override
	public void handleReemittionRtData(String ticketid, boolean isDelete) throws IOException {
		handleRtData(ticketid, isDelete);
	}

	private void emittTicketData(String ticketid, boolean isDelete) {
		LOG.debug("calling emittTicketData.....");
		Ticket incident = null;
		incident = rtAppClient.getTicket(ticketid);

		if (null == incident) {
			LOG.error("Obtaining a ticket for id={} failed!", ticketid);
			return;
		}

		LOG.debug("got incident id: {}", incident.getId());
		String uuid = incident.getCustomField(IncidentCustomFields.CF_RT_UUID);
		LOG.debug("got incident uuid: {}", uuid);

		DataParams dataParams = new DataParams();
		dataParams.setDateTime(new DateTime());

		List<Origin> origins = originService.findByOriginRecordId(uuid);
		if (origins.isEmpty()) {
			LOG.debug("Origin not found");
			dataParams.setOriginCspId(cspId);
			dataParams.setOriginApplicationId(APPLICATION_ID);
			dataParams.setOriginRecordId(uuid);
			dataParams.setCspId(cspId);
			dataParams.setApplicationId(APPLICATION_ID);
			dataParams.setRecordId(uuid);
		} else {
			LOG.debug("Origin found" + origins.toString());
			dataParams.setOriginCspId(origins.get(0).getOriginCspId());
			dataParams.setOriginApplicationId(origins.get(0).getOriginApplicationId());
			dataParams.setOriginRecordId(origins.get(0).getOriginRecordId());
			dataParams.setCspId(cspId);
			dataParams.setApplicationId(APPLICATION_ID);
			dataParams.setRecordId(uuid);
		}

		String dataparamURL = protocol + "://" + host + ":" + port + "/RTIR/Display.html?id=" + incident.getId();
		LOG.debug("Integration data:dataParams:url: " + dataparamURL);
		dataParams.setUrl(dataparamURL);

		IntegrationData integrationData = new IntegrationData();
		integrationData.setDataParams(dataParams);

		// set SharingParams
		SharingParams sharingParams = new SharingParams();
		sharingParams.setTcId(null);
		sharingParams.setTeamId(null);
		sharingParams.setIsExternal(false);

		String sharing = incident.getSharing();
		LOG.debug("setToShare:" + sharing);

		if (sharing.equalsIgnoreCase(CfSharing.DEFAULT_SHARING.toString())
				|| sharing.toLowerCase().contains(CfSharing.DEFAULT_SHARING.toString().toLowerCase())) {
			LOG.debug("SetToShare:true:" + sharing);
			sharingParams.setToShare(true);
		} else if (sharing.equalsIgnoreCase(CfSharing.NO_SHARING.toString())
				|| sharing.toLowerCase().contains(CfSharing.NO_SHARING.toString().toLowerCase())) {
			LOG.debug("SetToShare:false:" + sharing);
			sharingParams.setToShare(false);
		} else {
			LOG.debug("Build ids for TCs and Teams:" + sharing);
			sharingParams.setToShare(true);
			List<String> tcsandTeams = buildSharingTCsandTeamsList(sharing);
			LOG.debug("All TCs and Teams size:" + tcsandTeams.size());
			List<String> tcsIdlist = null;
			List<String> teamsIdlist = null;
			
			List<TrustCircle> ltcs = tcClient.getAllLocalTrustCircles();
			LOG.debug("All LocalTrustCircles size:" +ltcs.size());
			for (TrustCircle trustCircle : ltcs) {
				LOG.debug("All LocalTrustCircles:id" + trustCircle.getId() +" Name:"+ trustCircle.getName()+" ShortName:"+ trustCircle.getShortName());
			}
			
			List<TrustCircle> tcs = tcClient.getAllTrustCircles();
			LOG.debug("All LocalTrustCircles size:" +tcs.size());
			for (TrustCircle trustCircle : tcs) {
				LOG.debug("All TrustCircles:id" + trustCircle.getId() +" Name:"+ trustCircle.getName()+" ShortName:"+ trustCircle.getShortName());
			}
			
			if (tcsandTeams != null && tcsandTeams.size() > 0) {
				try {
					//UAT FIX UAT FIX 4.4.2018 tcsIdlist = tcClient.getAllLocalTrustCircles().stream()
					tcsIdlist = tcClient.getAllTrustCircles().stream()
							.filter(tc -> tcsandTeams.contains(tc.getShortName())).map(TrustCircle::getId)
							.collect(Collectors.toList());
				} catch (Exception e) {
					LOG.error("getAllTrustCircle from TC failed with: ", e);
					tcsIdlist = null;
				}

				if (tcsIdlist != null && tcsIdlist.size() == 0)
					tcsIdlist = null;

				if (tcsIdlist != null && tcsIdlist.size() > 0) {
					LOG.debug("trustCircle IDs size:" + tcsIdlist.size());
					for (int i = 0; i < tcsIdlist.size(); i++) {
						LOG.debug("trustCircle ID:" + tcsIdlist.get(i));
					}
				}
				sharingParams.setTcId(tcsIdlist);

				List<Team> teams = tcClient.getAllTeams();
				LOG.debug("All Teams size:" +teams.size());
				for (Team team : teams) {
					LOG.debug("All Teams:id" + team.getId() +" Name:"+ team.getName()+" ShortName:"+ team.getShortName());
				}
				
				try {
					//UAT FIX 4.4.2018 teamsIdlist = tcClient.getAllTeams().stream().filter(team -> tcsandTeams.contains(team.getName()))
					teamsIdlist = tcClient.getAllTeams().stream().filter(team -> tcsandTeams.contains(team.getShortName()))
							.map(Team::getId).collect(Collectors.toList());
				} catch (Exception e) {
					LOG.error("getAllTeams from TC failed with: ", e);
					teamsIdlist = null;
				}

				if (teamsIdlist != null && teamsIdlist.size() == 0)
					teamsIdlist = null;
				if (teamsIdlist != null && teamsIdlist.size() > 0) {
					LOG.debug("teams IDs size:" + teamsIdlist.size());
					for (int i = 0; i < teamsIdlist.size(); i++) {
						LOG.debug("team ID:" + teamsIdlist.get(i));
					}
				}
				sharingParams.setTeamId(teamsIdlist);
			}
		}

		integrationData.setSharingParams(sharingParams);

		JsonNode incidentjsonNode = getJsonNodeFromIncident(incident);

		integrationData.setDataObject(incidentjsonNode);

		IntegrationDataType integrationDataType = IntegrationDataType.INCIDENT;

		integrationData.setDataType(integrationDataType);
		LOG.debug("Integration data: " + integrationData.toString());

		boolean rtObjextExists = false;
		List<RTuuid> uuids = rtUuidService.findByRTUuid(uuid);
		if (uuids.isEmpty()) {
			rtObjextExists = false;
			RTuuid rtuuid = new RTuuid();
			rtuuid.setTid(incident.getId());
			rtuuid.setUuid(incident.getCustomField(IncidentCustomFields.CF_RT_UUID));
			LOG.debug("inserting a new data into local repository of known tickets:" + rtuuid.toString());
			rtUuidService.saveOrUpdate(rtuuid);
		} else {
			rtObjextExists = true;
		}

		LOG.debug("RT Object exists: " + rtObjextExists);

		if (isDelete) {
			LOG.debug("calling deleteIntegrationData ...");
			cspClient.deleteIntegrationData(integrationData);
		} else {
			try {
				if (rtObjextExists) {
					LOG.debug("RT EMITTER calling updateIntegrationData UPDATE.");
					cspClient.updateIntegrationData(integrationData);
				} else {
					LOG.debug("RT EMITTER calling postIntegrationData POST.");
					cspClient.postIntegrationData(integrationData);
				}
			} catch (Exception e) {
				LOG.error("Forward to IL failed with: ", e);
			}
		}
	}

	private List<String> buildSharingTCsandTeamsList(String sharing) {
		List<String> sharingpolicys = null;
		if (sharing != null && !sharing.isEmpty()) {
			LOG.debug("CustomField Sharing policy:" + sharing);
			sharingpolicys = Arrays.asList(sharing.split("\\s*,\\s*"));
		} else
			LOG.debug("CustomField Sharing policy is null or empty");
		return sharingpolicys;
	}

	private JsonNode getJsonNodeFromIncident(Ticket incident) {
		JsonNode incidentjsonNode = null;
		try {
			ObjectMapper mapper = new ObjectMapper().registerModule(new ParameterNamesModule())
					.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
			incidentjsonNode = mapper.convertValue(incident, JsonNode.class);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("getJsonNodeFromIncident:" + e.getMessage());
		}
		return incidentjsonNode;
	}

	@Override
	public void handleRtTestData(String ticketid, boolean b) {
		LOG.debug("calling handleRtTestData.....");
		String response = rtAppClient.callRtNewReportTest();
		LOG.debug("handleRtTestData callRtClientReport {}", response);
		String responseT = rtAppClient.callRtClientTicketsTest(ticketid);
		LOG.debug("handleRtTestData callRtClientTickets {}", responseT);
	}

	/*
	 * private boolean checkTicketHasChanged(String ticketid) { boolean
	 * bHaveNewChanges = true; List<RTuuid> rtUuids =
	 * rtUuidService.findByTicketId(ticketid); String tbody =
	 * rtAppClient.getTicketBody(ticketid); if (rtUuids.isEmpty()) { RTuuid rtuuid =
	 * new RTuuid(); rtuuid.setTid(ticketid); //rtuuid.setHc(tbody.hashCode());
	 * LOG.debug("inserting a new data into local repository of known ticket:" +
	 * rtuuid.toString()); rtUuidService.saveOrUpdate(rtuuid); } else { RTuuid
	 * rtUuid = rtUuids.get(0); if (rtUuid.getHc() == tbody.hashCode()) {
	 * LOG.debug("checkTicketHasChanged ticket has no changes:" + ticketid);
	 * bHaveNewChanges = false; } else {
	 * LOG.debug("checkTicketHasChanged ticket has new changes:" + ticketid); } }
	 * return bHaveNewChanges; }
	 */

}
