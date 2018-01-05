package com.intrasoft.csp.commons.model.elastic.query;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "filter"
})
public class Bool {

    @JsonProperty("filter")
    private List<Filter> filter = null;

    @JsonProperty("filter")
    public List<Filter> getFilter() {
        return filter;
    }

    @JsonProperty("filter")
    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "Bool{" +
                "filter=" + filter +
                '}';
    }

}
