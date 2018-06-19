package com.intrasoft.csp.commons.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Contact{

	@JsonProperty("public_usenet")
	private String publicUsenet;

	@JsonProperty("business_hours")
	private String businessHours;

	@JsonProperty("country")
	private String country;

	@JsonProperty("csp_id")
	private String cspId;

	@JsonProperty("nis_team_types")
	private List<String> nisTeamTypes;

	@JsonProperty("postal_country")
	private String postalCountry;

	@JsonProperty("business_hours_timezone")
	private String businessHoursTimezone;

	@JsonProperty("contact_postal_country")
	private String contactPostalCountry;

	@JsonProperty("public_ftp")
	private String publicFtp;

	@JsonProperty("nis_sectors")
	private List<String> nisSectors;

	@JsonProperty("public_www")
	private String publicWww;

	@JsonProperty("main_email")
	private String mainEmail;

	@JsonProperty("automated_email")
	private String automatedEmail;

	@JsonProperty("memberships")
	private String memberships;

	@JsonProperty("constituency_types")
	private List<String> constituencyTypes;

	@JsonProperty("scope_asns")
	private List<String> scopeAsns;

	@JsonProperty("host_organisation")
	private String hostOrganisation;

	@JsonProperty("email_visibility")
	private boolean emailVisibility;

	@JsonProperty("outside_business_hours")
	private String outsideBusinessHours;

	@JsonProperty("id")
	private String id;

	@JsonProperty("established_on")
	private String establishedOn;

	@JsonProperty("contact_postal_address")
	private String contactPostalAddress;

	@JsonProperty("additional_countries")
	private List<String> additionalCountries;

	@JsonProperty("email")
	private String email;

	@JsonProperty("postal_address")
	private String postalAddress;

	@JsonProperty("scope_ipranges")
	private List<String> scopeIpranges;

	@JsonProperty("public_mailinglist")
	private String publicMailinglist;

	@JsonProperty("ml_key")
	private String mlKey;

	@JsonProperty("constituency_asns")
	private List<String> constituencyAsns;

	@JsonProperty("ml_email")
	private String mlEmail;

	@JsonProperty("constituency_description")
	private String constituencyDescription;

	@JsonProperty("constituency_domains")
	private List<String> constituencyDomains;

	@JsonProperty("csp_domain")
	private String cspDomain;

	@JsonProperty("member_locations")
	private List<String> memberLocations;

	@JsonProperty("phone_numbers")
	private String phoneNumbers;

	@JsonProperty("automated_email_format")
	private List<String> automatedEmailFormat;

	@JsonProperty("full_name")
	private String fullName;

	@JsonProperty("certificates")
	private String certificates;

	@JsonProperty("constituency_ipranges")
	private List<String> constituencyIpranges;

	@JsonProperty("scope_email")
	private List<String> scopeEmail;

	@JsonProperty("csp_installed")
	private boolean cspInstalled;

	@JsonProperty("name")
	private String name;

	@JsonProperty("team_members")
	private String teamMembers;

	@JsonProperty("short_name")
	private String shortName;

	@JsonProperty("public_email")
	private String publicEmail;

	@JsonProperty("status")
	private String status;

	public void setPublicUsenet(String publicUsenet){
		this.publicUsenet = publicUsenet;
	}

	public String getPublicUsenet(){
		return publicUsenet;
	}

	public void setBusinessHours(String businessHours){
		this.businessHours = businessHours;
	}

	public String getBusinessHours(){
		return businessHours;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setCspId(String cspId){
		this.cspId = cspId;
	}

	public String getCspId(){
		return cspId;
	}

	public void setNisTeamTypes(List<String> nisTeamTypes){
		this.nisTeamTypes = nisTeamTypes;
	}

	public List<String> getNisTeamTypes(){
		return nisTeamTypes;
	}

	public void setPostalCountry(String postalCountry){
		this.postalCountry = postalCountry;
	}

	public String getPostalCountry(){
		return postalCountry;
	}

	public void setBusinessHoursTimezone(String businessHoursTimezone){
		this.businessHoursTimezone = businessHoursTimezone;
	}

	public String getBusinessHoursTimezone(){
		return businessHoursTimezone;
	}

	public void setContactPostalCountry(String contactPostalCountry){
		this.contactPostalCountry = contactPostalCountry;
	}

	public String getContactPostalCountry(){
		return contactPostalCountry;
	}

	public void setPublicFtp(String publicFtp){
		this.publicFtp = publicFtp;
	}

	public String getPublicFtp(){
		return publicFtp;
	}

	public void setNisSectors(List<String> nisSectors){
		this.nisSectors = nisSectors;
	}

	public List<String> getNisSectors(){
		return nisSectors;
	}

	public void setPublicWww(String publicWww){
		this.publicWww = publicWww;
	}

	public String getPublicWww(){
		return publicWww;
	}

	public void setMainEmail(String mainEmail){
		this.mainEmail = mainEmail;
	}

	public String getMainEmail(){
		return mainEmail;
	}

	public void setAutomatedEmail(String automatedEmail){
		this.automatedEmail = automatedEmail;
	}

	public String getAutomatedEmail(){
		return automatedEmail;
	}

	public void setMemberships(String memberships){
		this.memberships = memberships;
	}

	public String getMemberships(){
		return memberships;
	}

	public void setConstituencyTypes(List<String> constituencyTypes){
		this.constituencyTypes = constituencyTypes;
	}

	public List<String> getConstituencyTypes(){
		return constituencyTypes;
	}

	public void setScopeAsns(List<String> scopeAsns){
		this.scopeAsns = scopeAsns;
	}

	public List<String> getScopeAsns(){
		return scopeAsns;
	}

	public void setHostOrganisation(String hostOrganisation){
		this.hostOrganisation = hostOrganisation;
	}

	public String getHostOrganisation(){
		return hostOrganisation;
	}

	public void setEmailVisibility(boolean emailVisibility){
		this.emailVisibility = emailVisibility;
	}

	public boolean isEmailVisibility(){
		return emailVisibility;
	}

	public void setOutsideBusinessHours(String outsideBusinessHours){
		this.outsideBusinessHours = outsideBusinessHours;
	}

	public String getOutsideBusinessHours(){
		return outsideBusinessHours;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setEstablishedOn(String establishedOn){
		this.establishedOn = establishedOn;
	}

	public String getEstablishedOn(){
		return establishedOn;
	}

	public void setContactPostalAddress(String contactPostalAddress){
		this.contactPostalAddress = contactPostalAddress;
	}

	public String getContactPostalAddress(){
		return contactPostalAddress;
	}

	public void setAdditionalCountries(List<String> additionalCountries){
		this.additionalCountries = additionalCountries;
	}

	public List<String> getAdditionalCountries(){
		return additionalCountries;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setPostalAddress(String postalAddress){
		this.postalAddress = postalAddress;
	}

	public String getPostalAddress(){
		return postalAddress;
	}

	public void setScopeIpranges(List<String> scopeIpranges){
		this.scopeIpranges = scopeIpranges;
	}

	public List<String> getScopeIpranges(){
		return scopeIpranges;
	}

	public void setPublicMailinglist(String publicMailinglist){
		this.publicMailinglist = publicMailinglist;
	}

	public String getPublicMailinglist(){
		return publicMailinglist;
	}

	public void setMlKey(String mlKey){
		this.mlKey = mlKey;
	}

	public String getMlKey(){
		return mlKey;
	}

	public void setConstituencyAsns(List<String> constituencyAsns){
		this.constituencyAsns = constituencyAsns;
	}

	public List<String> getConstituencyAsns(){
		return constituencyAsns;
	}

	public void setMlEmail(String mlEmail){
		this.mlEmail = mlEmail;
	}

	public String getMlEmail(){
		return mlEmail;
	}

	public void setConstituencyDescription(String constituencyDescription){
		this.constituencyDescription = constituencyDescription;
	}

	public String getConstituencyDescription(){
		return constituencyDescription;
	}

	public void setConstituencyDomains(List<String> constituencyDomains){
		this.constituencyDomains = constituencyDomains;
	}

	public List<String> getConstituencyDomains(){
		return constituencyDomains;
	}

	public void setCspDomain(String cspDomain){
		this.cspDomain = cspDomain;
	}

	public String getCspDomain(){
		return cspDomain;
	}

	public void setMemberLocations(List<String> memberLocations){
		this.memberLocations = memberLocations;
	}

	public List<String> getMemberLocations(){
		return memberLocations;
	}

	public void setPhoneNumbers(String phoneNumbers){
		this.phoneNumbers = phoneNumbers;
	}

	public String getPhoneNumbers(){
		return phoneNumbers;
	}

	public void setAutomatedEmailFormat(List<String> automatedEmailFormat){
		this.automatedEmailFormat = automatedEmailFormat;
	}

	public List<String> getAutomatedEmailFormat(){
		return automatedEmailFormat;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setCertificates(String certificates){
		this.certificates = certificates;
	}

	public String getCertificates(){
		return certificates;
	}

	public void setConstituencyIpranges(List<String> constituencyIpranges){
		this.constituencyIpranges = constituencyIpranges;
	}

	public List<String> getConstituencyIpranges(){
		return constituencyIpranges;
	}

	public void setScopeEmail(List<String> scopeEmail){
		this.scopeEmail = scopeEmail;
	}

	public List<String> getScopeEmail(){
		return scopeEmail;
	}

	public void setCspInstalled(boolean cspInstalled){
		this.cspInstalled = cspInstalled;
	}

	public boolean isCspInstalled(){
		return cspInstalled;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setTeamMembers(String teamMembers){
		this.teamMembers = teamMembers;
	}

	public String getTeamMembers(){
		return teamMembers;
	}

	public void setShortName(String shortName){
		this.shortName = shortName;
	}

	public String getShortName(){
		return shortName;
	}

	public void setPublicEmail(String publicEmail){
		this.publicEmail = publicEmail;
	}

	public String getPublicEmail(){
		return publicEmail;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Contact{" + 
			"public_usenet = '" + publicUsenet + '\'' + 
			",business_hours = '" + businessHours + '\'' + 
			",country = '" + country + '\'' + 
			",csp_id = '" + cspId + '\'' + 
			",nis_team_types = '" + nisTeamTypes + '\'' +
			",postal_country = '" + postalCountry + '\'' + 
			",business_hours_timezone = '" + businessHoursTimezone + '\'' + 
			",contact_postal_country = '" + contactPostalCountry + '\'' + 
			",public_ftp = '" + publicFtp + '\'' + 
			",nis_sectors = '" + nisSectors + '\'' + 
			",public_www = '" + publicWww + '\'' + 
			",main_email = '" + mainEmail + '\'' + 
			",automated_email = '" + automatedEmail + '\'' + 
			",memberships = '" + memberships + '\'' + 
			",constituency_types = '" + constituencyTypes + '\'' + 
			",scope_asns = '" + scopeAsns + '\'' + 
			",host_organisation = '" + hostOrganisation + '\'' +
			",email_visibility = '" + emailVisibility + '\'' + 
			",outside_business_hours = '" + outsideBusinessHours + '\'' + 
			",id = '" + id + '\'' + 
			",established_on = '" + establishedOn + '\'' + 
			",contact_postal_address = '" + contactPostalAddress + '\'' + 
			",additional_countries = '" + additionalCountries + '\'' + 
			",email = '" + email + '\'' + 
			",postal_address = '" + postalAddress + '\'' + 
			",scope_ipranges = '" + scopeIpranges + '\'' + 
			",public_mailinglist = '" + publicMailinglist + '\'' + 
			",ml_key = '" + mlKey + '\'' + 
			",constituency_asns = '" + constituencyAsns + '\'' + 
			",ml_email = '" + mlEmail + '\'' + 
			",constituency_description = '" + constituencyDescription + '\'' + 
			",constituency_domains = '" + constituencyDomains + '\'' + 
			",csp_domain = '" + cspDomain + '\'' + 
			",member_locations = '" + memberLocations + '\'' + 
			",phone_numbers = '" + phoneNumbers + '\'' + 
			",automated_email_format = '" + automatedEmailFormat + '\'' + 
			",full_name = '" + fullName + '\'' + 
			",certificates = '" + certificates + '\'' + 
			",constituency_ipranges = '" + constituencyIpranges + '\'' + 
			",scope_email = '" + scopeEmail + '\'' + 
			",csp_installed = '" + cspInstalled + '\'' + 
			",name = '" + name + '\'' + 
			",team_members = '" + teamMembers + '\'' + 
			",short_name = '" + shortName + '\'' + 
			",public_email = '" + publicEmail + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}