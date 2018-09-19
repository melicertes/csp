package com.fraunhofer.csp.rt.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

//import javax.ws.rs.core.Cookie;
//import javax.ws.rs.core.HttpHeaders;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import java.io.UnsupportedEncodingException;

//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
//import org.apache.commons.httpclient.methods.multipart.Part;
//import org.apache.commons.httpclient.methods.multipart.StringPart;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraunhofer.csp.rt.client.RtClient;
import com.fraunhofer.csp.rt.client.RtElasticClient;
import com.fraunhofer.csp.rt.domain.model.RTuuid;
import com.fraunhofer.csp.rt.domain.service.impl.RTuuidServiceImpl;
import com.fraunhofer.csp.rt.ticket.CfSharing;
import com.fraunhofer.csp.rt.ticket.Links;
import com.fraunhofer.csp.rt.ticket.Ticket;
import com.fraunhofer.csp.rt.ticket.TicketListParser;
import com.intrasoft.csp.commons.model.IntegrationDataType;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Service
public class RtAppClient {

	private static final String LINKS_SEPERTATOR_CHAR = ",";

	final Logger LOG = LoggerFactory.getLogger(RtAppClient.class);

	private static final String PREFIX_ADAPTER = "adapter:";
	private static final String CF_REPORTER = "rt-adapter";
	private static final String CF_LINKED_VULNERABILITIES = "CF.{Linked vulnerabilities}: ";
	private static final String CF_LINKED_THREATS = "CF.{Linked threats}: ";
	private static final String CF_LINKED_EVENTS = "CF.{Linked events}: ";
	private static final String CF_ADDITIONAL_DATA = "CF.{Additional data}: ";
	private static final String CF_ORIGINATOR_CSP = "CF.{Originator CSP}: ";
	private static final String CF_LAST_UPDATE_DONE_BY = "CF.{Last update done by}: ";
	private static final String CF_HOW_REPORTED = "CF.{How Reported}: ";
	private static final String CF_REPORTER_TYPE = "CF.{Reporter Type}: ";
	private static final String RT_REMOTE_USER = "RT_REMOTE_USER";
	private static final String TICKET_SEARCH_PATH = "search/ticket";
	private static final String TICKET_PATH = "ticket/";

	@Value("${rt.app.username}")
	String username;

	@Value("${domain.name}")
	String doaminname;

	@Value("${csp.client.ssl.jks.keystore:path}")
	String cspClientSslJksKeystore;

	@Value("${csp.client.ssl.jks.keystore.password:securedPass}")
	String cspClientSslJksKeystorePassword;

	private WebTarget endPoint;
	private Client client;

	@Autowired
	RtElasticClient rtElasticClient;

	@Autowired
	RTuuidServiceImpl rtUuidService;

	@Autowired
	ResourcePatternResolver resourcePatternResolver;

	// TODO
	@Autowired
	RtClient rtClient;

	org.springframework.http.HttpStatus status;

	public RtAppClient() {
		// client = ClientBuilder.newClient().register(TicketListParser.class);
	}

	private KeyStore loadStore(String keyStoreFile, String keyStorePassword) throws Exception {
		LOG.debug("loadStore:keyStoreFile:" + keyStoreFile);

		InputStream keystoreInputStream = resourcePatternResolver.getResource(keyStoreFile).getInputStream();

		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(keystoreInputStream, keyStorePassword.toCharArray());

		return keyStore;
	}

	private List<Ticket> getTicketsForId(final String ticketid) {
		LOG.debug("get_tickets......");
		String context = rtClient.getRtURI();
		LOG.debug("get_tickets:" + context);
		ClientBuilder builder = ClientBuilder.newBuilder();

		KeyStore keyStore;
		try {
			keyStore = loadStore(cspClientSslJksKeystore, cspClientSslJksKeystorePassword);
			builder.keyStore(keyStore, cspClientSslJksKeystorePassword);
			client = builder.build();
			client.register(TicketListParser.class);
		} catch (Exception e) {
			LOG.error("get_tickets:");
			e.printStackTrace();
		}

		endPoint = client.target(context);
		try {
			return endPoint.path(TICKET_PATH + ticketid).request(MediaType.TEXT_PLAIN_TYPE)
					.header(RT_REMOTE_USER, this.username).get(new GenericType<List<Ticket>>() {
					});
		} catch (Exception ex) {
			LOG.error("get_tickets:" + ex.toString());
			return null;
		}
	}

	private Links getTicketLinksForId(final String ticketid) {
		LOG.debug("get_links ...");
		Links rv = null;
		do {
			try {
				String path = TICKET_PATH + ticketid + "/links";
				LOG.debug("working with path: {}", path);

				String strBody = null;
				try {
					ResponseEntity<String> responseEntity = rtClient.getContentData(path);
					status = responseEntity.getStatusCode();
					LOG.info(responseEntity.toString());
					strBody = responseEntity.getBody();
				} catch (Exception e) {
					LOG.error("getTicketLinksForId: an exception was thrown:" + e);
				}

				if (null == strBody || "".equals(strBody.trim())) {
					LOG.debug("getTicketLinksForId: got no message body from rt");
					return null;
				}
				BufferedReader br = new BufferedReader(new StringReader(strBody));
				String strLine = null;
				boolean bFound = false;
				boolean bHashNext = false;
				String strPattern = "fsck.com-rt";
				while (null != (strLine = br.readLine().trim())) {
					if (0 == strLine.trim().length())
						continue;
					if (strLine.startsWith("Members:")) {
						LOG.debug("index of pattern: {}", strLine.indexOf(strPattern, 0));
						String strTmp = strLine.substring(strLine.indexOf(strPattern, 0));
						bFound = true;
						bHashNext = strLine.endsWith(LINKS_SEPERTATOR_CHAR);
						strTmp = strTmp.substring(0, strTmp.length() - 1);
						if (null == rv)
							rv = new Links();
						LOG.debug("found another link: {}", strTmp);
						rv.addLink(strTmp);
						continue;
					}
					if (bFound && bHashNext) {
						bHashNext = strLine.endsWith(LINKS_SEPERTATOR_CHAR);
						String strTmp = strLine;
						if (bHashNext)
							strTmp = strLine.substring(0, strLine.length() - 1).trim();
						LOG.debug("getTicketLinksForId:found another link: {}", strTmp);
						rv.addLink(strTmp);
						continue;
					}
				}

				LOG.debug("getTicketLinksForId DONE.");
			} catch (Exception ex) {
				LOG.error(ex.toString());
			}
		} while (false);

		return rv;
	}

	public String getMessage(final String ticketid) {
		LOG.debug("working with ticketid: {}", ticketid);

		String Content = null;
		try {

			String path = TICKET_PATH + ticketid + "/attachments";
			LOG.debug("working with path: {}", path);

			String strBody = null;
			try {
				ResponseEntity<String> responseEntity = rtClient.getContentData(path);
				status = responseEntity.getStatusCode();
				LOG.info(responseEntity.toString());
				strBody = responseEntity.getBody();
			} catch (Exception e) {
				LOG.error("getMessage:an exception was thrown:", e);
			}

			if (null == strBody || "".equals(strBody.trim())) {
				LOG.debug("getMessage:got no message body from rt");
				return null;
			}

			int msgid = -1;
			String[] lines = strBody.split(System.getProperty("line.separator"));
			for (String line : lines) {
				if (line.contains("Attachments") && line.contains("(Unnamed) (text/html")) {
					try {
						String[] strs = line.split("\\:");
						msgid = Integer.parseInt(strs[1].trim());
						LOG.debug("getMessage:message_id:" + msgid);
						break;
					} catch (NumberFormatException e) {
					}
				}
			}
			if (msgid == -1) {
				LOG.debug("getMessage:got no ID forAttachments Unnamed text/html ");
				return null;
			}

			String strPathmsg = TICKET_PATH + ticketid + "/attachments/" + msgid;
			LOG.debug("getMessage:working with path: {}", strPathmsg);
			String strBodyMsg = null;
			try {
				ResponseEntity<String> responseEntity = rtClient.getContentData(strPathmsg);
				status = responseEntity.getStatusCode();
				LOG.info(responseEntity.toString());
				strBodyMsg = responseEntity.getBody();
			} catch (Exception e) {
				LOG.error("getMessage:an exception was thrown:", e);
			}

			if (null == strBodyMsg || "".equals(strBodyMsg.trim())) {
				LOG.debug("get_message:got no message body from rt");
				return null;
			}

			Content = strBodyMsg.substring(strBodyMsg.indexOf("Content:") + 9);

			if (null == Content || "".equals(Content.trim())) {
				LOG.debug("getMessage:got no Content response from rt");
				return null;
			}
			LOG.debug("getMessage done well with:" + Content);
		} catch (Exception ex) {
			LOG.error("getMessage:an exception was thrown:", ex);
		}

		return Content;
	}

	public String getTicketBody(final String ticketid) {
		LOG.debug("getTicketBody working with ticketid: {}", ticketid);
		String Content = null;
		try {

			String path = TICKET_PATH + ticketid;
			LOG.debug("working with path: {}", path);

			try {
				ResponseEntity<String> responseEntity = rtClient.getContentData(path);
				status = responseEntity.getStatusCode();
				LOG.info(responseEntity.toString());
				Content = responseEntity.getBody();
			} catch (Exception e) {
				LOG.error(e.getMessage());
				return Content;
			}

			String status = Content.substring(0, 15);
			if (status.equalsIgnoreCase("RT/4.4.2 200 Ok"))
				System.out.println("status:" + status);
			else {
				LOG.error("getTicketBody:status is NO OK");
				return null;
			}
			if (null == Content || "".equals(Content.trim())) {
				LOG.error("getTicketBody:got no body from rt");
				return null;
			}
		} catch (Exception ex) {
			LOG.error(ex.toString());
		}

		return Content;
	}

	public String updateRtReport(JsonNode eventJsonNode, String origin, String dataparamUrl,
			IntegrationDataType integrationDataType) {
		LOG.debug("updateRtReport starts.......................");
		String event_uuid = eventJsonNode.get("Event").get("uuid").toString().replace("\"", "");
		LOG.debug("updateRtReport:event_uuid:" + event_uuid);
		String info = eventJsonNode.get("Event").get("info").toString().replace("\"", "");
		LOG.debug("updateRtReport:info:" + info);

		String queue = RtQueues.INCIDENT_REPORTS_QUEUE.toString();
		LOG.debug("updateRtReport:queue:" + queue);

		String reportContent = "Queue: " + queue + "\n";
		reportContent = reportContent + "Owner: " + username + "\n";
		reportContent = reportContent + "Subject: " + "[" + origin + "] " + info + "\n";
		if (integrationDataType == IntegrationDataType.EVENT)
			reportContent = reportContent + CF_LINKED_EVENTS + dataparamUrl + "\n";
		else if (integrationDataType == IntegrationDataType.THREAT)
			reportContent = reportContent + CF_LINKED_THREATS + dataparamUrl + "\n";
		else
			reportContent = reportContent + CF_LINKED_VULNERABILITIES + dataparamUrl + "\n";

		// LOG.debug("updateRtReport:" + reportContent);
		String response = null;

		List<RTuuid> uuids = rtUuidService.findByRTUuid(event_uuid);
		if (uuids.isEmpty()) {
			LOG.error("No Reports found for this uuid:" + event_uuid);
			LOG.error("Cannot updateReport with uuid:" + event_uuid);
			return response;
		}

		String ticketId = uuids.get(0).getTid();
		LOG.debug("RT Object exists:id: " + ticketId);

		String updatePath = "/ticket/" + ticketId + "/edit?user=" + username;

		try {
			ResponseEntity<String> responseEntity = rtClient.postContentData(reportContent, updatePath);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			response = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("updateRtReport:an exception was thrown:" + e);
		}

		LOG.debug("updateRtReport:done with:" + response);

		return response;
	}

	public String addRtReport(String event_uuid, String subject, String origin, String dataparamUrl,
			IntegrationDataType integrationDataType) {

		LOG.debug("addRtReport starts.......................");
		LOG.debug("addRtReport:event_uuid:" + event_uuid);
		LOG.debug("addRtReport:subject:" + subject);

		String queue = RtQueues.INCIDENT_REPORTS_QUEUE.toString();
		LOG.debug("addRtReport:queue:" + queue);

		String reportContent = "Queue: " + queue + "\n";
		reportContent = reportContent + "Owner: " + username + "\n";
		reportContent = reportContent + "Subject: " + "[" + origin + "] " + subject + "\n";
		if (integrationDataType == IntegrationDataType.EVENT)
			reportContent = reportContent + CF_LINKED_EVENTS + dataparamUrl + "\n";
		else if (integrationDataType == IntegrationDataType.THREAT)
			reportContent = reportContent + CF_LINKED_THREATS + dataparamUrl + "\n";
		else
			reportContent = reportContent + CF_LINKED_VULNERABILITIES + dataparamUrl + "\n";

		reportContent = reportContent + CF_HOW_REPORTED + CF_REPORTER + "\n";
		reportContent = reportContent + CF_REPORTER_TYPE + CF_REPORTER + "\n";
		// LOG.debug("ReportContent:" + reportContent);

		String addReportPath = "/ticket/new?user=" + username;
		LOG.debug("addRtReport:" + addReportPath);

		String response = null;
		try {
			ResponseEntity<String> responseEntity = rtClient.postContentData(reportContent, addReportPath);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			response = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("addRtReport:an exception was thrown:" + e);
			return null;
		}

		LOG.debug("addRtReport:response:" + response);
		String ticketid = getTicketIdFromResponse(response);

		if (ticketid != null && !ticketid.isEmpty()) {
			LOG.debug("addRtReport:ticketid:" + ticketid);
			RTuuid rtuuid = new RTuuid();
			rtuuid.setTid(ticketid);
			rtuuid.setUuid(event_uuid);
			LOG.debug("addRtReport:inserting a new data into local repository of known reports:" + rtuuid.toString());
			rtUuidService.saveOrUpdate(rtuuid);
		} else
			LOG.error("addRtReport incidentId cannot be null or empty");

		LOG.debug("addRtReport DONE.");
		return response;
	}

	public String prettyPrintJsonString(JsonNode jsonNode) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(jsonNode.toString(), Object.class);
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		} catch (Exception e) {
			return "Sorry, pretty print didn't work";
		}
	}

	private String buildAndAdjustLinks(Ticket incident, String ticketContent) {
		LOG.debug("buildAndAdjustLinks:ticketContent:before:" + ticketContent);
		String linkedEvents = incident.getCustomField(IncidentCustomFields.CF_LINKED_EVENTS);
		if (linkedEvents != null && !linkedEvents.isEmpty()) {
			ticketContent = ticketContent + CF_LINKED_EVENTS + adjustLinks(linkedEvents, IntegrationDataType.EVENT)
					+ "\n";
		}
		String linkedThreats = incident.getCustomField(IncidentCustomFields.CF_LINKED_THREATS);
		if (linkedThreats != null && !linkedThreats.isEmpty()) {
			ticketContent = ticketContent + CF_LINKED_THREATS + adjustLinks(linkedThreats, IntegrationDataType.THREAT)
					+ "\n";
		}
		String linkedVulnerabilities = incident.getCustomField(IncidentCustomFields.CF_LINKED_VULNERABILITIES);
		if (linkedVulnerabilities != null && !linkedVulnerabilities.isEmpty()) {
			ticketContent = ticketContent + CF_LINKED_VULNERABILITIES
					+ adjustLinks(linkedVulnerabilities, IntegrationDataType.VULNERABILITY) + "\n";
		}

		LOG.debug("buildAndAdjustLinks:ticketContent:after:" + ticketContent);
		return ticketContent;
	}

	public String testbuildAndAdjustLinks(Ticket incident) {
		LOG.debug("testbuildAndAdjustLinks...");
		String linkedEvents = incident.getCustomField(IncidentCustomFields.CF_LINKED_EVENTS);
		String ticketContent = "";
		if (linkedEvents != null && !linkedEvents.isEmpty()) {
			ticketContent = ticketContent + CF_LINKED_EVENTS + adjustLinks(linkedEvents, IntegrationDataType.EVENT)
					+ "\n";
		}
		String linkedThreats = incident.getCustomField(IncidentCustomFields.CF_LINKED_THREATS);
		if (linkedThreats != null && !linkedThreats.isEmpty()) {
			ticketContent = ticketContent + CF_LINKED_THREATS + adjustLinks(linkedThreats, IntegrationDataType.THREAT)
					+ "\n";
		}
		String linkedVulnerabilities = incident.getCustomField(IncidentCustomFields.CF_LINKED_VULNERABILITIES);
		if (linkedVulnerabilities != null && !linkedVulnerabilities.isEmpty()) {
			ticketContent = ticketContent + CF_LINKED_VULNERABILITIES
					+ adjustLinks(linkedVulnerabilities, IntegrationDataType.VULNERABILITY) + "\n";
		}
		LOG.debug("testbuildAndAdjustLinks:ticketContent:" + ticketContent);
		return ticketContent;
	}

	private String adjustLinks(String cflinks, IntegrationDataType datatype) {
		LOG.debug("adjustLinks:cflinks:" + cflinks);
		String ticketContent = "";
		String[] urls = cflinks.split(("[\\s,;]+"));
		LOG.debug("adjustLinks:links count:" + urls.length);
		for (String url : urls) {
			URI uri;
			try {
				LOG.debug("adjustLinks:url:" + url);
				uri = new URI(url);
				String hostName = uri.getHost();
				LOG.debug("adjustLinks:hostName:" + hostName);
				String path = uri.getPath();
				LOG.debug("adjustLinks:path:" + path);
				String uuidStr = path.substring(path.lastIndexOf('/') + 1);
				LOG.debug("adjustLinks:UUID:" + uuidStr);
				try {
					UUID uuid = UUID.fromString(uuidStr);
					try {
						LOG.debug("adjustLinks:rtElasticClient.objectExists UUID:" + uuid.toString());
						if (rtElasticClient.objectExists(uuidStr, datatype)) {
							ticketContent = ticketContent + replacedomainname(url) + LINKS_SEPERTATOR_CHAR;
						} else
							LOG.debug("adjustLinks:NOT FOUND FOR UUID:" + uuidStr);
					} catch (IOException eio) {
						eio.printStackTrace();
						LOG.error("Failed to get objectExists from Elastic with: ", eio);
					} catch (Exception e) {
						e.printStackTrace();
						LOG.error("Failed to get objectExists from Elastic with: ", e);
					}
				} catch (IllegalArgumentException exception) {
					LOG.error("UUID NOT VALID in links:" + uuidStr);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
				LOG.error("URISyntaxException with:" + e);
			}
		}
		LOG.debug("adjustLinks:ticketContent:" + ticketContent);
		return ticketContent;
	}

	public String getHostname(String url) {
		LOG.debug("getHostname:url:" + url);
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String hostname = uri.getHost();

		int startIndex = hostname.indexOf('.');

		if (startIndex > 0) {
			hostname = hostname.substring(0, startIndex);
		}
		LOG.debug("getHostname:out:" + hostname);
		return hostname;
	}

	private String replacedomainname(String url) {
		LOG.debug("replacedomainname: url: {}", url);
		URL newURL = null;
		try {
			URL originalURL = new URL(url);
			String newdomainname = getHostname(url) + "." + doaminname;
			newURL = new URL(originalURL.getProtocol(), newdomainname, originalURL.getPort(), originalURL.getFile());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		LOG.debug("replacedomainname: new url: {}", newURL.toString());
		return newURL.toString();
	}

	@SuppressWarnings("unused")
	private void addLink2Incident(String response, Ticket incident) {

		LOG.debug("addLink2Incident: response: {}", response);

		String ticketid = getTicketIdFromResponse(response);

		String linkId = incident.getId();
		LOG.debug("addLink2Incident: incidentId: {}", ticketid);
		LOG.debug("addLink2Incident: linkId: {}", linkId);

		if (ticketid == null || ticketid.isEmpty()) {
			LOG.error("addLink2Incident incidentId cannot be null or empty");
			return;
		}
		if (linkId == null || linkId.isEmpty()) {
			LOG.error("addLink2Incident linkId cannot be null or empty");
			return;
		}

		String content = "id: ticket/" + ticketid + "links\nMemberOf: fsck.com-rt://example.com/ticket/" + linkId;
		LOG.debug("addLink2Incident:content" + content);

		String linksTicketPath = "/ticket/" + ticketid + "/links?user=" + username;
		LOG.debug("addLink2Incident:" + linksTicketPath);

		String res = null;
		try {
			ResponseEntity<String> responseEntity = rtClient.postContentData(content, linksTicketPath);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			res = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("addLink2Incident:an exception was thrown:" + e);
		}
		LOG.debug("addLink2Incident:done with:" + res);
	}

	private String getTicketIdFromResponse(String response) {
		// LOG.debug("getTicketIdFromResponse: response: {}", response);
		LOG.debug("getTicketIdFromResponse.....");
		int responseTicketId = -1;
		String[] lines = response.split(System.getProperty("line.separator"));
		for (String line : lines) {
			if (line.contains("created") && line.contains("Ticket")) {
				String[] strs = line.split("\\s+");
				try {
					responseTicketId = Integer.parseInt(strs[2]);
				} catch (NumberFormatException e) {
					responseTicketId = -1;
				}
			}
		}
		String ticketid = null;
		if (responseTicketId != -1) {
			ticketid = Integer.toString(responseTicketId);
			LOG.debug("getTicketIdFromResponse: got ticketid: {}", ticketid);
		} else {
			LOG.debug("getTicketIdFromResponse no ticketid from rt response");
			return null;
		}
		return ticketid;
	}

	public void addMessage2Incident(String response, String msg) {
		LOG.debug("addMessage2Incident: orig msg: {}", msg);
		msg = msg.trim();
		LOG.debug("addMessage2Incident: trimed msg: {}", msg);
		String ticketid = getTicketIdFromResponse(response);
		if (ticketid == null || ticketid.isEmpty()) {
			LOG.error("addMessage2Incident incident Id cannot be null or empty");
			return;
		}

		String content = "id: " + ticketid + "\nAction: comment\nText: " + msg;
		LOG.debug("addMessage2Incident:" + content);

		String path = "/ticket/" + ticketid + "/comment?user=" + username;
		LOG.debug("addMessage2Incident:" + path);

		String res = null;
		try {
			ResponseEntity<String> responseEntity = rtClient.postContentData(content, path);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			res = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("addMessage2Incident:an exception was thrown:" + e);
		}

		LOG.debug("addMessage2Incident:done with:" + res);

	}

	private String buildTicketContent(Ticket incident, String queue, String originplusApplicationId, boolean isUpdate) {
		LOG.debug("buildTicketContent:originplusApplicationId:" + originplusApplicationId);
		LOG.debug("buildTicketContent:isUpdate:" + isUpdate);
		String ticketContent = "Queue: " + queue + "\n";
		ticketContent = ticketContent + "Status: " + incident.getStatus().toString().toLowerCase() + "\n";

		ticketContent = ticketContent + "Subject: " + incident.getSubject() + "\n";
		ticketContent = ticketContent + "Owner: " + username + "\n";

		ticketContent = ticketContent + "CF.{Description}: "
				+ incident.getCustomField(IncidentCustomFields.CF_DESCRIPTION) + "\n";
		ticketContent = ticketContent + "CF.{Function}: " + incident.getCustomField(IncidentCustomFields.CF_FUNCTION)
				+ "\n";
		ticketContent = ticketContent + "CF.{Resolution}: "
				+ incident.getCustomField(IncidentCustomFields.CF_RESOLUTION) + "\n";
		ticketContent = ticketContent + "CF.{IP}: " + incident.getCustomField(IncidentCustomFields.CF_IP) + "\n";
		ticketContent = ticketContent + "CF.{Classification}: "
				+ incident.getCustomField(IncidentCustomFields.CF_CLASSIFICATION) + "\n";
		ticketContent = ticketContent + CF_ADDITIONAL_DATA
				+ incident.getCustomField(IncidentCustomFields.CF_ADDITIONAL_DATA) + "\n";
		ticketContent = ticketContent + "CF.{Sharing policy}: " + CfSharing.NO_SHARING.toString() + "\n";

		ticketContent = ticketContent + CF_ORIGINATOR_CSP
				+ incident.getCustomField(IncidentCustomFields.CF_ORIGINATOR_CSP) + "\n";

		String ludb;
		if (isUpdate) {
			LOG.debug("buildTicketContent:isUpdate:" + isUpdate);
			ludb = CF_LAST_UPDATE_DONE_BY + PREFIX_ADAPTER + originplusApplicationId;
			LOG.debug("buildTicketContent:isUpdate:{}", ludb);
		} else {
			ludb = CF_LAST_UPDATE_DONE_BY + incident.getCustomField(IncidentCustomFields.CF_LAST_UPDATE_DONE_BY);
			LOG.debug("buildTicketContent:noUpdate:{}", ludb);
		}

		ticketContent = ticketContent + ludb + "\n";

		LOG.debug("buildTicketContent: {}", ticketContent);
		return ticketContent;
	}

	public String addRtTicket(Ticket incident, String originplusApplicationId, String queue) {

		// TODO: should we also handle the linked data (countermeasures, investigations
		// and incident reports)?
		LOG.debug("addRtTicket:id:{} queue:{} origin:{} ", incident.getId(), queue, originplusApplicationId);
		String ticketContent = buildTicketContent(incident, queue, originplusApplicationId, false);
		ticketContent = buildAndAdjustLinks(incident, ticketContent);

		ticketContent = ticketContent + "CF.{RT_UUID}: " + PREFIX_ADAPTER
				+ incident.getCustomField(IncidentCustomFields.CF_RT_UUID) + "\n";

		String addTicketPath = "/ticket/new?user=" + username;
		LOG.debug("addRtTicket:" + addTicketPath);

		String response = null;
		try {
			ResponseEntity<String> responseEntity = rtClient.postContentData(ticketContent, addTicketPath);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			response = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("addRtTicket:an exception was thrown:" + e);
			return response;
		}

		// # Ticket 775 created.
		String msg = incident.getMessage();
		if (msg != null && !msg.isEmpty())
			// add message to incident
			addMessage2Incident(response, msg);
		// if (queue.equalsIgnoreCase(RtQueues.INCIDENT_REPORTS_QUEUE.toString()))
		// this ticket was a incident report, we do not link automatically, operator has
		// to do this manually
		// addLink2Incident(response, incident);
		LOG.debug("addRtTicket DONE.");
		return response;
	}

	public String updateRtTicket(Ticket incident, String ticketId, String originplusApplicationId) {
		LOG.debug("updateRtTicket ticketId:{} originplusApplicationId:{} ", ticketId, originplusApplicationId);

		String content = buildTicketContent(incident, incident.getQueue(), originplusApplicationId, true);
		content = buildAndAdjustLinks(incident, content);

		String path = "/ticket/" + ticketId + "/edit?user=" + username;
		LOG.debug("updateRtTicket:" + path);
		String response = null;
		try {
			ResponseEntity<String> responseEntity = rtClient.postContentData(content, path);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			response = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("updateRtTicket:an exception was thrown:" + e);
		}
		return response;
	}

	public String getTicketIdForUUID(Ticket incident) {
		LOG.debug("getTicketIdForUUID starts....................");
		String uuid = incident.getCustomField(IncidentCustomFields.CF_RT_UUID);
		if (uuid == null || uuid.isEmpty())
			return null;
		else
			LOG.debug("getTicketIdForUUID:" + uuid);
		String strBody = null;

		String path = TICKET_SEARCH_PATH;
		LOG.debug("getTicketIdForUUID working with path: {}", path);
		try {
			String query = "'CF.{RT_UUID}'='" + uuid + "'&Queue=Incidents&fields=id";
			ResponseEntity<String> responseEntity = rtClient.getContentData(query, path);
			status = responseEntity.getStatusCode();
			LOG.info(responseEntity.toString());
			strBody = responseEntity.getBody();
		} catch (Exception e) {
			LOG.error("getTicketIdForUUID:an exception was thrown:", e);
			LOG.error("getTicketIdForUUID:couldn't get the id  for uuid {}.", uuid);
			return null;
		}

		LOG.debug("getTicketIdForUUID: got response: {}", strBody);

		if (null == strBody || "".equals(strBody.trim())) {
			LOG.debug("getTicketIdForUUID: got no response from rt");
			return null;
		}
		int id = -1;
		String[] lines = strBody.split(System.getProperty("line.separator"));
		for (String line : lines) {
			if (line.toLowerCase().contains("id:".toLowerCase())) {
				String tStr = line.substring(line.lastIndexOf("/") + 1);
				try {
					id = Integer.parseInt(tStr);
					break;
				} catch (NumberFormatException e) {
					id = -1;
				}
			}
		}
		if (id != -1) {
			String ticketid = Integer.toString(id);
			LOG.debug("getTicketIdForUUID got ticketid: {}", ticketid);
			return ticketid;
		} else {
			LOG.debug("getTicketIdForUUID got no ticketid from rt");
			return null;
		}
	}

	public Ticket getTicket(String ticketid) {
		LOG.debug("getTicket:" + ticketid);

		final List<Ticket> tickets = getTicketsForId(ticketid);
		if (tickets == null) {
			LOG.debug("getTicket: Found NO ticket(s) for id {} ", ticketid);
			return null;
		}

		LOG.error("getTicket: Found {} ticket(s)", tickets.size());

		Ticket incident = null;
		for (Ticket t : tickets) {
			if (t.getId().equals(String.valueOf(ticketid))) {
				// printer.print(t);
				incident = t;
				break;
			}
		}
		if (incident != null) {
			String msg = getMessage(incident.getId());
			if (msg != null && !msg.isEmpty())
				incident.setMessage(msg);
		}
		return incident;
	}

	public List<Ticket> getLinks(final String ticketid) {
		LOG.debug("getLinks for ticket with id= {}.", ticketid);
		List<Ticket> rv = null;
		do {
			// final Links links = auth(username, password).get_links(ticketid);
			final Links links = getTicketLinksForId(ticketid);
			if (null == links) {
				LOG.error("couldn't get links data for ticket with id={}.", ticketid);
				break;
			}

			List<String> lLinks = links.getMembersExtern("http");
			if (null == lLinks) {
				LOG.error("getLinks:couldn't get the links url for ticket {}.", ticketid);
				break;
			}
			LOG.debug("getLinks:got {} of links for ticket {}.", lLinks.size(), ticketid);
			rv = new ArrayList<Ticket>();

			for (final ListIterator<String> iter = lLinks.listIterator(); iter.hasNext();) {
				final String _strURL = iter.next();
				final String _ticketId = _strURL.substring(_strURL.lastIndexOf("/") + 1).trim();
				LOG.debug("getLinks:obtaining a ticket data for ticketid={}", _ticketId);
				Ticket ticket = getTicket(ticketid);
				if (null == ticket) {
					LOG.warn("getLinks:couldn't obtain a ticket for id={} comming from link={}", _ticketId, _strURL);
					continue;
				}
				rv.add(ticket);
			}
			if (0 == rv.size()) {
				LOG.warn("getLinks:no linked tickets could be obtained!");
				rv = null;
			}

		} while (false);

		return rv;
	}

	public String callRtNewReportTest() {
		LOG.debug("callRtNewReportTest....................");
		String queue = RtQueues.INCIDENT_REPORTS_QUEUE.toString();
		LOG.debug("callRtNewReportTest:queue:" + queue);
		String reportContent = "Queue: " + queue + "\n";
		reportContent = reportContent + "Owner: " + username + "\n";
		reportContent = reportContent + "Subject: " + "TEST :Subject" + "\n";
		reportContent = reportContent + CF_HOW_REPORTED + CF_REPORTER + "\n";
		reportContent = reportContent + CF_REPORTER_TYPE + CF_REPORTER + "\n";
		String testpath = "/ticket/new?user=" + username;
		LOG.debug("callRtNewReportTest:" + testpath);
		ResponseEntity<String> response = rtClient.postContentData(reportContent, testpath);
		response.getStatusCode();
		LOG.debug("callRtNewReportTest:" + response.getStatusCode());
		String responseBody = response.getBody();
		LOG.debug("callRtNewReportTest:" + responseBody);
		return "OK";
	}

	public String callRtClientTicketsTest(String tid) {
		LOG.debug("callRtClientTicketsTest....................");
		String testpath = TICKET_PATH + tid;
		LOG.debug("callRtClientTicketsTest:" + testpath);
		ResponseEntity<String> response = rtClient.getTickets(testpath);
		response.getStatusCode();
		LOG.debug("callRtClientTicketsTest:" + response.getStatusCode());
		String responseBody = response.getBody();
		LOG.debug("callRtClientTicketsTest:" + responseBody);
		return "OK";
	}

}
