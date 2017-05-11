package com.sastix.csp.commons.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iskitsas on 4/11/17.
 */
@Deprecated
public class TrustCircleEcspDTO implements Serializable {
    private static final long serialVersionUID = 9056613423375285570L;
    TrustCircle trustCircle;
    List<Team> teams = new ArrayList<Team>();
    IntegrationData integrationData;

    public TrustCircleEcspDTO() {
    }

    public TrustCircleEcspDTO(TrustCircle trustCircle, IntegrationData integrationData) {
        this.trustCircle = trustCircle;
        this.integrationData = integrationData;
    }

    public TrustCircleEcspDTO(TrustCircle trustCircle, Team team, IntegrationData integrationData) {
        this.trustCircle = trustCircle;
        this.teams.add(team);
        this.integrationData = integrationData;
    }

    public TrustCircleEcspDTO(TrustCircle trustCircle, ArrayList<Team> teams, IntegrationData integrationData) {
        this.trustCircle = trustCircle;
        this.teams= teams;
        this.integrationData = integrationData;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public void setTeam(Team team) {
        this.teams.add(team);
    }

    public TrustCircle getTrustCircle() {
        return trustCircle;
    }

    public void setTrustCircle(TrustCircle trustCircle) {
        this.trustCircle = trustCircle;
    }

    public IntegrationData getIntegrationData() {
        return integrationData;
    }

    public void setIntegrationData(IntegrationData integrationData) {
        this.integrationData = integrationData;
    }

    @Override
    public String toString() {
        return "TrustCircleEcspDTO{" +
                "trustCircle=" + trustCircle +
                ", teams=" + teams +
                ", integrationData=" + integrationData +
                '}';
    }
}
