package com.intrasoft.csp.commons.model.elastic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.intrasoft.csp.commons.model.elastic.query.Query;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "query"
})
public class ElasticDelete {

    @JsonProperty("query")
    private Query query;

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
        return "ElasticDeleteObject{" +
                "query=" + query +
                '}';
    }

}
