package com.intrasoft.csp.conf.clientcspapp.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user",
        "password",
        "db",
        "db_cfg",
        "docr_port"
})
public class Postgres {
    @JsonProperty("user")
    public String user;
    @JsonProperty("password")
    public String password;
    @JsonProperty("db")
    public String db;
    @JsonProperty("db_cfg")
    public String dbCfg;
    @JsonProperty("docr_port")
    public String docrPort;

}