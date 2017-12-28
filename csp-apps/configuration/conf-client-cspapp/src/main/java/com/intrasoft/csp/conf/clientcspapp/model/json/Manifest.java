package com.intrasoft.csp.conf.clientcspapp.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "format",
        "sh-first",
        "sh-last",
        "config"
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Manifest {
    @JsonProperty("format")
    public double format;
    @JsonProperty("sh-first")
    public String shFirst;
    @JsonProperty("sh-last")
    public String shLast;
    @JsonProperty("config")
    @Valid
    public Config config;

}
