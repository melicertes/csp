package com.intrasoft.csp.anon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 12/7/2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "rules"
})
public class Rules {

    @JsonProperty("rules")
    public List<Rule> rules = new ArrayList<>();

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rules{");
        sb.append("rules=").append(rules);
        sb.append('}');
        return sb.toString();
    }
}