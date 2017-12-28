package com.intrasoft.csp.conf.clientcspapp.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "external",
        "internal"
})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MutualSsl {

    @JsonProperty("external")
    public boolean external;
    @JsonProperty("internal")
    public boolean internal;

}