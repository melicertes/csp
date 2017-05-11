package com.sastix.csp.commons.model;

import java.io.Serializable;

/**
 * Created by iskitsas on 5/10/17.
 */
public class EnhancedTeamDTO implements Serializable {
    private static final long serialVersionUID = -6396447984119333708L;
    Team team;
    IntegrationData integrationData;

    public EnhancedTeamDTO() {
    }

    public EnhancedTeamDTO(Team team, IntegrationData integrationData) {
        this.team = team;
        this.integrationData = integrationData;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public IntegrationData getIntegrationData() {
        return integrationData;
    }

    public void setIntegrationData(IntegrationData integrationData) {
        this.integrationData = integrationData;
    }
}
