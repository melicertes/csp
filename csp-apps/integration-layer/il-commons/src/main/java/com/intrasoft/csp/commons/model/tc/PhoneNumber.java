package com.intrasoft.csp.commons.model.tc;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "tag",
        "number",
        "timezone",
        "number_details",
        "visibility"
})
public class PhoneNumber {

    @JsonProperty("tag")
    private String tag;
    @JsonProperty("number")
    private String number;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("number_details")
    private String numberDetails;
    @JsonProperty("visibility")
    private String visibility;


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getNumberDetails() {
        return numberDetails;
    }

    public void setNumberDetails(String numberDetails) {
        this.numberDetails = numberDetails;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

}
