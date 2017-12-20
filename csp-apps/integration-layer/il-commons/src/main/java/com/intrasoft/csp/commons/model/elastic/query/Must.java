package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "match_phrase"
})
public class Must {

    @JsonProperty("match_phrase")
    private Match match;


    @JsonProperty("match_phrase")
    public Match getMatch() {
        return match;
    }

    @JsonProperty("match_phrase")
    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return "Must{" +
                "match_phrase=" + match +
                '}';
    }
}
