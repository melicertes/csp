package com.sastix.csp.commons.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "short_name",
        "name",
        "description",
        "auth_source",
        "info_url",
        "membership_url",
        "teams",
        "created"
})
public class TrustCircle implements Serializable{

    private static final long serialVersionUID = 7473879403462995916L;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("short_name")
    private String shortName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("auth_source")
    private String authSource;
    @JsonProperty("info_url")
    private String infoUrl;
    @JsonProperty("membership_url")
    private String membershipUrl;
    @JsonProperty("teams")
    private List<Integer> teams = null;
    @JsonProperty("created")
    private String created;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("short_name")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("short_name")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("auth_source")
    public String getAuthSource() {
        return authSource;
    }

    @JsonProperty("auth_source")
    public void setAuthSource(String authSource) {
        this.authSource = authSource;
    }

    @JsonProperty("info_url")
    public String getInfoUrl() {
        return infoUrl;
    }

    @JsonProperty("info_url")
    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    @JsonProperty("membership_url")
    public String getMembershipUrl() {
        return membershipUrl;
    }

    @JsonProperty("membership_url")
    public void setMembershipUrl(String membershipUrl) {
        this.membershipUrl = membershipUrl;
    }

    @JsonProperty("teams")
    public List<Integer> getTeams() {
        return teams;
    }

    @JsonProperty("teams")
    public void setTeams(List<Integer> teams) {
        this.teams = teams;
    }

    @JsonProperty("created")
    public String getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(String created) {
        this.created = created;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "TrustCircle{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", authSource='" + authSource + '\'' +
                ", infoUrl='" + infoUrl + '\'' +
                ", membershipUrl='" + membershipUrl + '\'' +
                ", teams=" + teams +
                ", created='" + created + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    public List<String> getCsps(){

        return new ArrayList<>();
    }
}

