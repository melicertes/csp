package com.intrasoft.csp.regrep.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticCountResponse{

	@JsonProperty("_shards")
	private Shards shards;

	@JsonProperty("count")
	private int count;

	public void setShards(Shards shards){
		this.shards = shards;
	}

	public Shards getShards(){
		return shards;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	@Override
 	public String toString(){
		return 
			"ElasticCountResponse{" + 
			"_shards = '" + shards + '\'' + 
			",count = '" + count + '\'' + 
			"}";
		}
}