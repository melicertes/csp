package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Range{

	@JsonProperty("@timestamp")
	private Timestamp timestamp;

	public void setTimestamp(Timestamp timestamp){
		this.timestamp = timestamp;
	}

	public Timestamp getTimestamp(){
		return timestamp;
	}

	@Override
 	public String toString(){
		return 
			"Range{" + 
			"@timestamp = '" + timestamp + '\'' + 
			"}";
		}
}