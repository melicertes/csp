package com.intrasoft.csp.conf.clientcspapp.model.json;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "internal_name",
        "external_name",
        "version",
        "docr_name",
        "docr_port",
        "protocol",
        "central_only",
        "base_path",
        "skip_reverse_proxy_paths",
        "mutual_ssl",
        "agent",
        "paths",
        "env_properties"
})
@Getter @Setter @EqualsAndHashCode @ToString
@NoArgsConstructor @AllArgsConstructor
public class Service {

    @JsonProperty("internal_name")
    public String internalName;
    @JsonProperty("external_name")
    public String externalName;
    @JsonProperty("version")
    public String version;
    @JsonProperty("docr_name")
    public String docrName;
    @JsonProperty("docr_port")
    public String docrPort;
    @JsonProperty("protocol")
    public String protocol;
    @JsonProperty("central_only")
    public String centralOnly;
    @JsonProperty("base_path")
    public String basePath;
    @JsonProperty("skip_reverse_proxy_paths")
    @Valid
    public List<String> skipReverseProxyPaths = new ArrayList<>();
    @JsonProperty("mutual_ssl")
    @Valid
    public MutualSsl mutualSsl;

    @JsonProperty("agent")
    public boolean agent;

    @JsonProperty("paths")
    @Valid
    public Map<String, String> paths = new HashMap<>();

    @JsonProperty("env_properties")
    @Valid
    public Map<String, String> envProperties = new HashMap<>();

}