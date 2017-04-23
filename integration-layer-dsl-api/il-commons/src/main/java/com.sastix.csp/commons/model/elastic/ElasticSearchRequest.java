package com.sastix.csp.commons.model.elastic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sastix.csp.commons.model.elastic.query.Query;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "fields",
        "query"
})
public class ElasticSearchRequest {

    @JsonProperty("fields")
    private List<String> fields = null;

    @JsonProperty("query")
    private Query query;

    @JsonProperty("fields")
    public List<String> getFields() {
        return fields;
    }

    @JsonProperty("fields")
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @JsonProperty("query")
    public Query getQuery() {
        return query;
    }

    @JsonProperty("query")
    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "ElasticSearchRequest{" +
                "fields=" + fields +
                ", query=" + query +
                '}';
    }
}
