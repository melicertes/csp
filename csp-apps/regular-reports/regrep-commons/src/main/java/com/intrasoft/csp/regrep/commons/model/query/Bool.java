package com.intrasoft.csp.regrep.commons.model.query;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Bool{

	@JsonProperty("must")
	private List<MustItem> must;

	public void setMust(List<MustItem> must){
		this.must = must;
	}

	public List<MustItem> getMust(){
		return must;
	}

	@Override
 	public String toString(){
		return 
			"Bool{" + 
			"must = '" + must + '\'' + 
			"}";
		}
}