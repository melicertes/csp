package com.intrasoft.csp.conf.clientcspapp.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "key",
        "descr",
        "type",
        "required"
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Datum {

    @JsonProperty("key")
    public String key;
    @JsonProperty("descr")
    public String descr;
    @JsonProperty("type")
    public String type;
    @JsonProperty("required")
    public boolean required;

}
