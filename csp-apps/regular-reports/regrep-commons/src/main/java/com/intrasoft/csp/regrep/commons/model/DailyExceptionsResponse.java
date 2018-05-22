package com.intrasoft.csp.regrep.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DailyExceptionsResponse{

	@JsonProperty("_shards")
	private Shards shards;

	@JsonProperty("hits")
	private Hits hits;

	@JsonProperty("took")
	private int took;

	@JsonProperty("timed_out")
	private boolean timedOut;

	public void setShards(Shards shards){
		this.shards = shards;
	}

	public Shards getShards(){
		return shards;
	}

	public void setHits(Hits hits){
		this.hits = hits;
	}

	public Hits getHits(){
		return hits;
	}

	public void setTook(int took){
		this.took = took;
	}

	public int getTook(){
		return took;
	}

	public void setTimedOut(boolean timedOut){
		this.timedOut = timedOut;
	}

	public boolean isTimedOut(){
		return timedOut;
	}

	@Override
 	public String toString(){
		return 
			"DailyExceptionsResponse{" + 
			"_shards = '" + shards + '\'' + 
			",hits = '" + hits + '\'' + 
			",took = '" + took + '\'' + 
			",timed_out = '" + timedOut + '\'' + 
			"}";
		}
}