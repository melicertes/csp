package com.intrasoft.csp.commons.model.tc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "tag",
        "method",
        "keyid",
        "visibility",
        "data"
})
public class Certificate implements Serializable {

    @JsonProperty("tag")
    private String tag;
    @JsonProperty("method")
    private String method;
    @JsonProperty("keyid")
    private String keyId;
    @JsonProperty("visibility")
    private String visibility;
    @JsonProperty("data")
    private String data;


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getKeyid() {
        return keyId;
    }

    public void setKeyid(String keyid) {
        this.keyId = keyid;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
