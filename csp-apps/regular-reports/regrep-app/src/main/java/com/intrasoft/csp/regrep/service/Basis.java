package com.intrasoft.csp.regrep.service;

public enum Basis {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

    @Override
    public String toString() {
        String name = this.name();
        return name.substring(0,1) + name.substring(1,name.length()).toLowerCase();
    }

    public String getDescription() {
        switch (this) {
            case DAILY: return "yesterday";
            case WEEKLY: return "last week";
            case MONTHLY: return "last month";
            case QUARTERLY: return "in the last 3 months";
            case YEARLY: return "last year";
        }
        return null;
    }
}
