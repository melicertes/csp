package com.intrasoft.csp.client;

public enum DateMath {
    NOW("now"),
    ONE_DAY("1d"),
    ONE_MONTH("1m"),
    ONE_YEAR("1y");

    private final String value;

    DateMath(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
