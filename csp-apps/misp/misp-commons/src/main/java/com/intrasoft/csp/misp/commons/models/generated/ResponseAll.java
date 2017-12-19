package com.intrasoft.csp.misp.commons.models.generated;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseAll{

	@JsonProperty("response")
	private List<Response> response;

	public void setResponse(List<Response> response){
		this.response = response;
	}

	public List<Response> getResponse(){
		return response;
	}

	@Override
 	public String toString(){
		return 
			"ResponseAll{" + 
			"response = '" + response + '\'' + 
			"}";
		}
}