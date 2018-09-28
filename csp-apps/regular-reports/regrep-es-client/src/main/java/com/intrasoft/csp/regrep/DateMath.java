package com.intrasoft.csp.regrep;

public enum DateMath {
    NOW("now"),
    DAY("d"),
    WEEK("w"),
    MONTH("M"),
    YEAR("y"),
    ONE_DAY("1d"),
    ONE_WEEK("1w"),
    ONE_MONTH("1M"),
    THREE_MONTHS("3M"),
    ONE_YEAR("1y"),
    MINUS("-"),
    PLUS("+"),
    RBTS_OF("/"); // Round Back To Start Of <month> or <day> etc.

    private final String value;

    DateMath(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
