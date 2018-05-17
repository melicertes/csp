package com.intrasoft.csp.regrep.commons.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Hits{

	@JsonProperty("hits")
	private List<HitsItem> hits;

	@JsonProperty("total")
	private int total;

	@JsonProperty("max_score")
	private double maxScore;

	public void setHits(List<HitsItem> hits){
		this.hits = hits;
	}

	public List<HitsItem> getHits(){
		return hits;
	}

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

	public void setMaxScore(double maxScore){
		this.maxScore = maxScore;
	}

	public double getMaxScore(){
		return maxScore;
	}

	@Override
 	public String toString(){
		return 
			"Hits{" + 
			"hits = '" + hits + '\'' + 
			",total = '" + total + '\'' + 
			",max_score = '" + maxScore + '\'' + 
			"}";
		}
}