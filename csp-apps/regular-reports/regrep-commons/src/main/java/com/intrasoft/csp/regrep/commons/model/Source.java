package com.intrasoft.csp.regrep.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source{

	@JsonProperty("@timestamp")
	private String timestamp;

	@JsonProperty("program")
	private String program;

	@JsonProperty("message")
	private String message;

	public void setTimestamp(String timestamp){
		this.timestamp = timestamp;
	}

	public String getTimestamp(){
		return timestamp;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	@Override
	public String toString() {
		return "Source{" +
				"timestamp='" + timestamp + '\'' +
				", program='" + program + '\'' +
				", message='" + message + '\'' +
				'}';
	}
}