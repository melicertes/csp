package com.instrasoft.csp.ccs.domain.data;


import java.util.List;

public class DashboardRow {

    private String icon;
    private String name;
    private String domain;
    private String ts;
    private String status;
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

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
                ", ts='" + ts + '\'' +
                ", status='" + status + '\'' +
                ", confUpdates=" + confUpdates +
                ", reportUpdates=" + reportUpdates +
                ", btn='" + btn + '\'' +
                '}';
    }
}
