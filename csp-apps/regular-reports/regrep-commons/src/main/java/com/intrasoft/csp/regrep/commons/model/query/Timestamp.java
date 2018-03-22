package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Timestamp{

	@JsonProperty("lt")
	private String lt;

	@JsonProperty("gte")
	private String gte;

	public void setLt(String lt){
		this.lt = lt;
	}

	public String getLt(){
		return lt;
	}

	public void setGte(String gte){
		this.gte = gte;
	}

	public String getGte(){
		return gte;
	}

	@Override
 	public String toString(){
		return 
			"Timestamp{" + 
			"lt = '" + lt + '\'' + 
			",gte = '" + gte + '\'' + 
			"}";
		}
}