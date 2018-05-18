package com.intrasoft.csp.regrep.service.impl;

import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.service.DateMathStringBuilder;
import org.springframework.stereotype.Component;

@Component
public class DateMathStringBuilderImpl implements DateMathStringBuilder {

    public DateMathStringBuilderImpl() {
    }

    @Override
    public String buildStringPattern(DateMath... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (DateMath dateMath : args) {
            stringBuilder.append(dateMath);
        }
        return stringBuilder.toString();
    }
}
