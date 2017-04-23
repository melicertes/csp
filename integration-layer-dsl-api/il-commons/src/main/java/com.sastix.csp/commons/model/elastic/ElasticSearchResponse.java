package com.sastix.csp.commons.model.elastic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sastix.csp.commons.model.elastic.search.Hits;
import com.sastix.csp.commons.model.elastic.search.Shards;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "took",
        "timed_out",
        "_shards",
        "hits"
})
public class ElasticSearchResponse {

    @JsonProperty("took")
    private Integer took;
    @JsonProperty("timed_out")
    private Boolean timedOut;
    @JsonProperty("_shards")
    private Shards shards;
    @JsonProperty("hits")
    private Hits hits;


    @JsonProperty("took")
    public Integer getTook() {
        return took;
    }

    @JsonProperty("took")
    public void setTook(Integer took) {
        this.took = took;
    }

    @JsonProperty("timed_out")
    public Boolean getTimedOut() {
        return timedOut;
    }

    @JsonProperty("timed_out")
    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }

    @JsonProperty("_shards")
    public Shards getShards() {
        return shards;
    }

    @JsonProperty("_shards")
    public void setShards(Shards shards) {
        this.shards = shards;
    }

    @JsonProperty("hits")
    public Hits getHits() {
        return hits;
    }

    @JsonProperty("hits")
    public void setHits(Hits hits) {
        this.hits = hits;
    }
}