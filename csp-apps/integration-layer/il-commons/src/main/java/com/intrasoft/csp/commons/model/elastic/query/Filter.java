package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "term"
})
public class Filter {

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
        return "Filter{" +
                "term=" + term +
                '}';
    }
}
