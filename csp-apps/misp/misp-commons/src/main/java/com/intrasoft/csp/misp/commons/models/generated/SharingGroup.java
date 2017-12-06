package com.intrasoft.csp.misp.commons.models.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class SharingGroup{

	@JsonProperty("releasability")
	private String releasability;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("id")
	private String id;

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("local")
	private boolean local;

	public void setReleasability(String releasability){
		this.releasability = releasability;
	}

	public String getReleasability(){
		return releasability;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setActive(boolean active){
		this.active = active;
	}

	public boolean isActive(){
		return active;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setUuid(String uuid){
		this.uuid = uuid;
	}

	public String getUuid(){
		return uuid;
	}

	public void setLocal(boolean local){
		this.local = local;
	}

	public boolean isLocal(){
		return local;
	}

	@Override
 	public String toString(){
		return 
			"SharingGroup{" + 
			"releasability = '" + releasability + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",active = '" + active + '\'' + 
			",id = '" + id + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",local = '" + local + '\'' + 
			"}";
		}
}