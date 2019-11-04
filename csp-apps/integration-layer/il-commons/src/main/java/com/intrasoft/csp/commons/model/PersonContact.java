package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.intrasoft.csp.commons.model.tc.*;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "full_name",
        "email",
        "email_visibility",
        "postal_address",
        "postal_country",
        "ml_email",
        "ml_key",
        "phone_numbers",
        "certificates",
        "memberships"
})
public class PersonContact {

    @JsonProperty("id")
    private String id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("email")
    private String email;
    /**
     * @apiNote CMM API yaml denotes this as Boolean but API returns String, i.e. "private"
     */
    @JsonProperty("email_visibility")
    private String emailVisibility;
    @JsonProperty("postal_address")
    private String postalAddress;
    @JsonProperty("postal_country")
    private String postalCountry;
    @JsonProperty("ml_email")
    private String mlEmail;
    @JsonProperty("ml_key")
    private String mlKey;
    @JsonProperty("phone_numbers")
    private List<PhoneNumber> phoneNumbers;
    @JsonProperty("certificates")
    private List<Certificate> certificates;
    @JsonProperty("memberships")
    private List<Membership> memberships;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVisibility() {
        return emailVisibility;
    }

    public void setEmailVisibility(String emailVisibility) {
        this.emailVisibility = emailVisibility;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPostalCountry() {
        return postalCountry;
    }

    public void setPostalCountry(String postalCountry) {
        this.postalCountry = postalCountry;
    }

    public String getMlEmail() {
        return mlEmail;
    }

    public void setMlEmail(String mlEmail) {
        this.mlEmail = mlEmail;
    }

    public String getMlKey() {
        return mlKey;
    }

    public void setMlKey(String mlKey) {
        this.mlKey = mlKey;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }
}
