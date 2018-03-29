package com.intrasoft.csp.regrep.service;

public enum Basis {
    DAILY("0 0 9 ? * MON-FRI"),      // Every weekday at 9 AM.
    WEEKLY("0 0 9 ? * MON *"),       // Every Monday  at 9 AM.
    MONTHLY("0 0 9 1W * ? *"),       // Every month, on the nearest weekday to the 1st of the month, at 9 AM.
    QUARTERLY("0 0 9 1W 1/3 ? *"),   // Every 3 months, starting in January, at 9 AM, on the nearest weekday to the 1st of the month.
    YEARLY("0 0 9 1W JAN ? 2019/1"); // Every year, starting in 2019, at 9 AM, on the nearest weekday to the 1st of the month, in January.

    private final String value;

    Basis(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Basis{" +
                "value='" + value + '\'' +
                '}';
    }
}
