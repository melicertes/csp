package com.fraunhofer.csp.rt.client;

/**
 * Created by Majid Salehi on 4/8/17.
 */
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public interface RtClient {
	public String getContext();

	public void setProtocolHostPort(String protocol, String host, String port, String username);

	ResponseEntity<String> postContentData(String content, String path, String key, String value);

	ResponseEntity<String> getContentData(String query, String path);

	ResponseEntity<String> getContentData(String path);

	ResponseEntity<String> getTickets(String path);

	UriComponents constructUriWithPathParameter(String path);

	UriComponents constructUriWithQueryParameter(String path, String query);

	UriComponentsBuilder constructUriWithQueryParameters(String path, String key, String value);

}
