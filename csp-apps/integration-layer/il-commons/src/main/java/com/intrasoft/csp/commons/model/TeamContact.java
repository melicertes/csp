package com.intrasoft.csp.commons.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamContact {

    private String id;
    private String cspId;
    private String cspDomain;
    private boolean cspInstalled;
    private List<String> nisTeamTypes;
    private List<String> nisSectors;
    private String status;
    private String shortName;
    private String name;
    private String hostOrganisation;
    private String country;
    private List<String> additionalCountries;
    private String establishedOn;
    private String constituencyTypes;
    private String constituencyDescription;
    private List<String> memberLocations;
    private List<String> constituencyAsns;
    private List<String> constituencyDomains;
    private List<String> constituencyIpranges;
    private List<String> scopeAsns;
    private List<String> scopeIpranges;
    private List<String> scopeEmail;
    private String contactPostalAddress;
    private String contactPostalCountry;
    private String phoneNumbers;
    private String mainEmail;
    private String publicEmail;
    private String automatedEmail;
    private List<String> automatedEmailFormat;
    private String publicWww;
    private String publicFtp;
    private String publicMailingList;
    private String publicUsenet;
    private String businessHours;
    private String outsideBusinessHours;
    private String businessHoursTimezone;
    private String teamMembers;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostOrganisation() {
        return hostOrganisation;
    }

    public void setHostOrganisation(String hostOrganisation) {
        this.hostOrganisation = hostOrganisation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getAdditionalCountries() {
        return additionalCountries;
    }

    public void setAdditionalCountries(List<String> additionalCountries) {
        this.additionalCountries = additionalCountries;
    }

    public String getEstablishedOn() {
        return establishedOn;
    }

    public void setEstablishedOn(String establishedOn) {
        this.establishedOn = establishedOn;
    }

    public String getConstituencyTypes() {
        return constituencyTypes;
    }

    public void setConstituencyTypes(String constituencyTypes) {
        this.constituencyTypes = constituencyTypes;
    }

    public String getConstituencyDescription() {
        return constituencyDescription;
    }

    public void setConstituencyDescription(String constituencyDescription) {
        this.constituencyDescription = constituencyDescription;
    }

    public List<String> getMemberLocations() {
        return memberLocations;
    }

    public void setMemberLocations(List<String> memberLocations) {
        this.memberLocations = memberLocations;
    }

    public List<String> getConstituencyAsns() {
        return constituencyAsns;
    }

    public void setConstituencyAsns(List<String> constituencyAsns) {
        this.constituencyAsns = constituencyAsns;
    }

    public List<String> getConstituencyDomains() {
        return constituencyDomains;
    }

    public void setConstituencyDomains(List<String> constituencyDomains) {
        this.constituencyDomains = constituencyDomains;
    }

    public List<String> getConstituencyIpranges() {
        return constituencyIpranges;
    }

    public void setConstituencyIpranges(List<String> constituencyIpranges) {
        this.constituencyIpranges = constituencyIpranges;
    }

    public List<String> getScopeAsns() {
        return scopeAsns;
    }

    public void setScopeAsns(List<String> scopeAsns) {
        this.scopeAsns = scopeAsns;
    }

    public List<String> getScopeIpranges() {
        return scopeIpranges;
    }

    public void setScopeIpranges(List<String> scopeIpranges) {
        this.scopeIpranges = scopeIpranges;
    }

    public List<String> getScopeEmail() {
        return scopeEmail;
    }

    public void setScopeEmail(List<String> scopeEmail) {
        this.scopeEmail = scopeEmail;
    }

    public String getContactPostalAddress() {
        return contactPostalAddress;
    }

    public void setContactPostalAddress(String contactPostalAddress) {
        this.contactPostalAddress = contactPostalAddress;
    }

    public String getContactPostalCountry() {
        return contactPostalCountry;
    }

    public void setContactPostalCountry(String contactPostalCountry) {
        this.contactPostalCountry = contactPostalCountry;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getMainEmail() {
        return mainEmail;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public String getPublicEmail() {
        return publicEmail;
    }

    public void setPublicEmail(String publicEmail) {
        this.publicEmail = publicEmail;
    }

    public String getAutomatedEmail() {
        return automatedEmail;
    }

    public void setAutomatedEmail(String automatedEmail) {
        this.automatedEmail = automatedEmail;
    }

    public List<String> getAutomatedEmailFormat() {
        return automatedEmailFormat;
    }

    public void setAutomatedEmailFormat(List<String> automatedEmailFormat) {
        this.automatedEmailFormat = automatedEmailFormat;
    }

    public String getPublicWww() {
        return publicWww;
    }

    public void setPublicWww(String publicWww) {
        this.publicWww = publicWww;
    }

    public String getPublicFtp() {
        return publicFtp;
    }

    public void setPublicFtp(String publicFtp) {
        this.publicFtp = publicFtp;
    }

    public String getPublicMailingList() {
        return publicMailingList;
    }

    public void setPublicMailingList(String publicMailingList) {
        this.publicMailingList = publicMailingList;
    }

    public String getPublicUsenet() {
        return publicUsenet;
    }

    public void setPublicUsenet(String publicUsenet) {
        this.publicUsenet = publicUsenet;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public String getOutsideBusinessHours() {
        return outsideBusinessHours;
    }

    public void setOutsideBusinessHours(String outsideBusinessHours) {
        this.outsideBusinessHours = outsideBusinessHours;
    }

    public String getBusinessHoursTimezone() {
        return businessHoursTimezone;
    }

    public void setBusinessHoursTimezone(String businessHoursTimezone) {
        this.businessHoursTimezone = businessHoursTimezone;
    }

    public String getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(String teamMembers) {
        this.teamMembers = teamMembers;
    }


}
