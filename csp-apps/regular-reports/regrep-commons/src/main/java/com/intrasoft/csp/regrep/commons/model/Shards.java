package com.intrasoft.csp.regrep.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Shards{

	@JsonProperty("total")
	private int total;

	@JsonProperty("failed")
	private int failed;

	@JsonProperty("successful")
	private int successful;

	@JsonProperty("skipped")
	private int skipped;

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

	public void setFailed(int failed){
		this.failed = failed;
	}

	public int getFailed(){
		return failed;
	}

	public void setSuccessful(int successful){
		this.successful = successful;
	}

	public int getSuccessful(){
		return successful;
	}

	public void setSkipped(int skipped){
		this.skipped = skipped;
	}

	public int getSkipped(){
		return skipped;
	}

	@Override
 	public String toString(){
		return 
			"Shards{" + 
			"total = '" + total + '\'' + 
			",failed = '" + failed + '\'' + 
			",successful = '" + successful + '\'' + 
			",skipped = '" + skipped + '\'' + 
			"}";
		}
}