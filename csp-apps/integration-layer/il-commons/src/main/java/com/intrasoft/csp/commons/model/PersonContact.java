package com.intrasoft.csp.commons.model;

import java.util.List;

public class PersonContact {

    private String id;
    private String cspId;
    private String cspDomain;
    private boolean cspInstalled;
    private List<String> nisTeamTypes;
    private List<String> nisSectors;
    private String fullName;
    private String email;
    private boolean emailVisibility;
    private String postalAddress;
    private String postalCountry;
    private String mlEmail;
    private String mlKey;
    private String phoneNumbers;
    private String certificates;
    private String memberships;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public String getCspDomain() {
        return cspDomain;
    }

    public void setCspDomain(String cspDomain) {
        this.cspDomain = cspDomain;
    }

    public boolean isCspInstalled() {
        return cspInstalled;
    }

    public void setCspInstalled(boolean cspInstalled) {
        this.cspInstalled = cspInstalled;
    }

    public List<String> getNisTeamTypes() {
        return nisTeamTypes;
    }

    public void setNisTeamTypes(List<String> nisTeamTypes) {
        this.nisTeamTypes = nisTeamTypes;
    }

    public List<String> getNisSectors() {
        return nisSectors;
    }

    public void setNisSectors(List<String> nisSectors) {
        this.nisSectors = nisSectors;
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

    public boolean isEmailVisibility() {
        return emailVisibility;
    }

    public void setEmailVisibility(boolean emailVisibility) {
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

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public String getMemberships() {
        return memberships;
    }

    public void setMemberships(String memberships) {
        this.memberships = memberships;
    }
}
