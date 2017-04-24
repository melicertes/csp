package com.sastix.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "must"
})
public class Bool {

    @JsonProperty("must")
    private List<Must> must = null;

    @JsonProperty("must")
    public List<Must> getMust() {
        return must;
    }

    @JsonProperty("must")
    public void setMust(List<Must> must) {
        this.must = must;
    }

    @Override
    public String toString() {
        return "Bool{" +
                "must=" + must +
                '}';
    }

}
