package com.intrasoft.csp.regrep.service;

public enum Basis {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY;

    @Override
    public String toString() {
        String name = this.name();
        return name.substring(0,1) + name.substring(1,name.length()).toLowerCase();
    }
}
