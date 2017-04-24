package com.sastix.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "term"
})
public class Must {

    @JsonProperty("term")
    private Term term;


    @JsonProperty("term")
    public Term getTerm() {
        return term;
    }

    @JsonProperty("term")
    public void setTerm(Term term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "Must{" +
                "term=" + term +
                '}';
    }
}
