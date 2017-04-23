package com.sastix.csp.commons.model.elastic.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "total",
        "successful",
        "failed"
})
public class Shards {

    @JsonProperty("total")
    private Integer total;
    @JsonProperty("successful")
    private Integer successful;
    @JsonProperty("failed")
    private Integer failed;

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    @JsonProperty("successful")
    public Integer getSuccessful() {
        return successful;
    }

    @JsonProperty("successful")
    public void setSuccessful(Integer successful) {
        this.successful = successful;
    }

    @JsonProperty("failed")
    public Integer getFailed() {
        return failed;
    }

    @JsonProperty("failed")
    public void setFailed(Integer failed) {
        this.failed = failed;
    }

}
