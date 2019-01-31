package com.fraunhofer.csp.rt.client.impl;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fraunhofer.csp.rt.client.RtClient;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public class RtClientImpl implements RtClient {
	private Logger LOG = (Logger) LoggerFactory.getLogger(RtClientImpl.class);

	@Value("${rt.app.protocol}")
	String rtProtocol;
	@Value("${rt.app.rest.path}")
	String rtRestPath;
	@Value("${rt.app.username}")
	String username;

	@Value("${rt.host}")
	String rtHost;
	@Value("${rt.port}")
	String rtPort;
	@Value("${rt.path}")
	String rtPath;

	private static final String RT_REMOTE_USER = "RT_REMOTE_USER";

	@Autowired
	@Qualifier("RtRestTemplate")
	RetryRestTemplate retryRestTemplate;

	String context;
	HttpHeaders headers;

	@Override
	public String getContext() {
		return context;
	}

	@Override
	public void setProtocolHostPort(String protocol, String host, String port, String username) {
		context = protocol + "://" + host + ":" + port;
		headers = new HttpHeaders();
		headers.add(RT_REMOTE_USER, username);
	}

	@Override
	public ResponseEntity<String> postContentData(String content, String path, String key, String value) {
		String url = this.constructUriWithQueryParameters(path, key, value).toUriString();
		// String url = this.getRtURI() + "/" + path;
		LOG.debug("API call postContentData [url]: " + url);
		// LOG.debug("API call postContentData [headers]: " + headers);
		// LOG.debug("API call postContentData [content]: " + content);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("content", content);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(parts,
				headers);

		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		response = retryRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		return response;
	}

	@Override
	public ResponseEntity<String> getContentData(String query, String path) {
		String url = this.constructUriWithPathParameter(path).toUriString();
		// String url = this.getRtURI() + "/" + path;
		LOG.debug("API call getContentData [url]: " + url);
		LOG.debug("API call getContentData [query]: " + query);
		LOG.debug("API call getContentData [headers]: " + headers);

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
				// Add query parameter
				.queryParam("query", query);
		UriComponents components = builder.build(false);
		URI uri = components.toUri();
		LOG.debug("API call getContentData [uri]: " + uri);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		response = retryRestTemplate.exchange(uri, HttpMethod.GET, request, String.class);
		return response;
	}

	@Override
	public ResponseEntity<String> getContentData(String path) {
		String url = this.constructUriWithPathParameter(path).toUriString();
		// String url = this.getRtURI() + "/" + path;
		LOG.debug("API call getContentData [url]: " + url);
		// LOG.debug("API call getContentData [headers]: " + headers);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		response = retryRestTemplate.exchange(url, HttpMethod.GET, request, String.class);
		return response;
	}

	@Override
	public ResponseEntity<String> getTickets(String path) {
		String url = this.constructUriWithPathParameter(path).toUriString();
		// String url = this.getRtURI() + "/" + path;
		LOG.debug("API call getTickets [url]: " + url);
		headers.add("format", "l");
		// LOG.debug("API call getTickets [headers]: " + headers);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		response = retryRestTemplate.exchange(url, HttpMethod.GET, request, String.class);
		return response;
	}

	public UriComponents constructUriWithPathParameter(String path) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(rtProtocol).host(rtHost).port(rtPort)
				.path(rtPath + "/" + rtRestPath + "/" + path).build();
		LOG.debug("constructUriWithPathParameter: uriComponents.toUriString()=" + uriComponents.toUriString());
		return uriComponents;

	}

	public UriComponents constructUriWithQueryParameter(String path, String query) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(rtProtocol).host(rtHost).port(rtPort)
				.path(rtPath + "/" + rtRestPath + "/" + path).query("q={keyword}").buildAndExpand(query);

		LOG.debug("constructUriWithQueryParameter: uriComponents.toUriString()=" + uriComponents.toUriString());
		return uriComponents;

	}

	public UriComponentsBuilder constructUriWithQueryParameters(String path, String key, String value) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(rtProtocol).host(rtHost)
				.port(rtPort).path(rtPath + "/" + rtRestPath + "/" + path).queryParam(key, value);
 
		LOG.debug("constructUriWithQueryParameters: uriComponentsBuilder.toUriString()="
				+ uriComponentsBuilder.toUriString());
		return uriComponentsBuilder;

	}

//	public String getRtURI() {
//		return rtProtocol + "://" + rtHost + ":" + rtPort + "/" + rtPath + "/" + rtRestPath;
//	}

}
