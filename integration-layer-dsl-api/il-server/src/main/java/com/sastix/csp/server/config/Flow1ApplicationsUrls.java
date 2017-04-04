package com.sastix.csp.server.config;

import com.sastix.csp.commons.model.IntegrationDataType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("flow1")
public class Flow1ApplicationsUrls {

    private List<String> eventApps = new ArrayList<>();
    private List<String> threatApps = new ArrayList<>();
    private List<String> incidentApps = new ArrayList<>();
    private List<String> vulnerabilityApps = new ArrayList<>();
    private List<String> artefactApps = new ArrayList<>();

    public List<String> getEventApps() {
        return eventApps;
    }

    public void setEventApps(List<String> eventApps) {
        this.eventApps = eventApps;
    }

    public List<String> getThreatApps() {
        return threatApps;
    }

    public void setThreatApps(List<String> threatApps) {
        this.threatApps = threatApps;
    }

    public List<String> getIncidentApps() {
        return incidentApps;
    }

    public void setIncidentApps(List<String> incidentApps) {
        this.incidentApps = incidentApps;
    }

    public List<String> getVulnerabilityApps() {
        return vulnerabilityApps;
    }

    public void setVulnerabilityApps(List<String> vulnerabilityApps) {
        this.vulnerabilityApps = vulnerabilityApps;
    }

    public List<String> getArtefactApps() {
        return artefactApps;
    }

    public void setArtefactApps(List<String> artefactApps) {
        this.artefactApps = artefactApps;
    }

    public List<String> getAppListByDataType(IntegrationDataType datatype) {
        switch (datatype) {
            case EVENT:
                return eventApps;
            case THREAT:
                return threatApps;
            case INCIDENT:
                return incidentApps;
            case VULNERABILITY:
                return vulnerabilityApps;
            case ARTEFACT:
                return artefactApps;
            default:
                return null;
        }
    }
}
