package com.intrasoft.csp.conf.server.domain.data.table;


import java.util.List;

public class DashboardRow {

    private String icon;
    private String name;
    private String domain;
    private String registrationDate;
    private String lastUpdate;
    private String lastManaged;
    private List<String> confUpdates;
    private List<String> reportUpdates;
    private String btn;


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastManaged() {
        return lastManaged;
    }

    public void setLastManaged(String lastManaged) {
        this.lastManaged = lastManaged;
    }

    public List<String> getConfUpdates() {
        return confUpdates;
    }

    public void setConfUpdates(List<String> confUpdates) {
        this.confUpdates = confUpdates;
    }

    public List<String> getReportUpdates() {
        return reportUpdates;
    }

    public void setReportUpdates(List<String> reportUpdates) {
        this.reportUpdates = reportUpdates;
    }

    public String getBtn() {
        return btn;
    }

    public void setBtn(String btn) {
        this.btn = btn;
    }


    @Override
    public String toString() {
        return "DashboardRow{" +
                "icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", lastManaged='" + lastManaged + '\'' +
                ", confUpdates=" + confUpdates +
                ", reportUpdates=" + reportUpdates +
                ", btn='" + btn + '\'' +
                '}';
    }
}
