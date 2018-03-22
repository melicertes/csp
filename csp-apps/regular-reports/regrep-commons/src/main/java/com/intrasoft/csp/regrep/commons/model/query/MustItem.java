package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MustItem{

	@JsonProperty("range")
	private Range range;

	public void setRange(Range range){
		this.range = range;
	}

	public Range getRange(){
		return range;
	}

	@Override
 	public String toString(){
		return 
			"MustItem{" + 
			"range = '" + range + '\'' + 
			"}";
		}
}