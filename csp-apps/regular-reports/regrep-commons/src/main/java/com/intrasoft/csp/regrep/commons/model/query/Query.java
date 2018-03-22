package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
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