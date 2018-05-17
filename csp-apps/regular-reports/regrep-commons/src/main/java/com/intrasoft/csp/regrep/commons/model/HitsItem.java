package com.intrasoft.csp.regrep.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HitsItem{

	@JsonProperty("_index")
	private String index;

	@JsonProperty("_type")
	private String type;

	@JsonProperty("_source")
	private Source source;

	@JsonProperty("_id")
	private String id;

	@JsonProperty("_score")
	private double score;

	public void setIndex(String index){
		this.index = index;
	}

	public String getIndex(){
		return index;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setSource(Source source){
		this.source = source;
	}

	public Source getSource(){
		return source;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setScore(double score){
		this.score = score;
	}

	public double getScore(){
		return score;
	}

	@Override
 	public String toString(){
		return 
			"HitsItem{" + 
			"_index = '" + index + '\'' + 
			",_type = '" + type + '\'' + 
			",_source = '" + source + '\'' + 
			",_id = '" + id + '\'' + 
			",_score = '" + score + '\'' + 
			"}";
		}
}