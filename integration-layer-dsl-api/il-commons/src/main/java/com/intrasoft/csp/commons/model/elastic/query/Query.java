package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bool"
})
public class Query {

    @JsonProperty("bool")
    private Bool bool;

    @JsonProperty("bool")
    public Bool getBool() {
        return bool;
    }

    @JsonProperty("bool")
    public void setBool(Bool bool) {
        this.bool = bool;
    }

    @Override
    public String toString() {
        return "Query{" +
                "bool=" + bool +
                '}';
    }

}