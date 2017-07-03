package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "match"
})
public class Must {

    @JsonProperty("match")
    private Match match;


    @JsonProperty("match")
    public Match getMatch() {
        return match;
    }

    @JsonProperty("match")
    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return "Must{" +
                "match=" + match +
                '}';
    }
}
