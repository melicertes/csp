package com.intrasoft.csp.commons.model.tc;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "organisation",
        "membership_state",
        "since"
})
public class Membership {

    @JsonProperty("organisation")
    private String organisation;
    @JsonProperty("membership_state")
    private String membershipState;
    @JsonProperty("since")
    private String since;


    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getMembershipState() {
        return membershipState;
    }

    public void setMembershipState(String membershipState) {
        this.membershipState = membershipState;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }
}
