package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Query{

	@JsonProperty("bool")
	private Bool bool;

	public void setBool(Bool bool){
		this.bool = bool;
	}

	public Bool getBool(){
		return bool;
	}

	@Override
 	public String toString(){
		return 
			"Query{" + 
			"bool = '" + bool + '\'' + 
			"}";
		}
}