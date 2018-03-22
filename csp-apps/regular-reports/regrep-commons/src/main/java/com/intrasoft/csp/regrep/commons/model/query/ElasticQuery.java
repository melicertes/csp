package com.intrasoft.csp.regrep.commons.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ElasticQuery{

	@JsonProperty("query")
	private Query query;

	public void setQuery(Query query){
		this.query = query;
	}

	public Query getQuery(){
		return query;
	}

	@Override
 	public String toString(){
		return 
			"ElasticQuery{" + 
			"query = '" + query + '\'' + 
			"}";
		}
}