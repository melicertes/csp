package com.intrasoft.csp.regrep.esclient.test;

import com.intrasoft.csp.regrep.service.DateMathStringBuilder;
import com.intrasoft.csp.regrep.service.impl.DateMathStringBuilderImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static com.intrasoft.csp.regrep.DateMath.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DateMathStringBuilderImpl.class})
public class DateMathStringBuilderTest {

    @Autowired
    DateMathStringBuilder dateMathStringBuilder;

    String dailyGte, weeklyGte, monthlyGte, quarterlyGte, yearlyGte, lt;

    // The expected patterns have been tested in Elasticsearch queries using Curl with successful results.
    // This test should assert that the method generates the expected String patterns using the provided DateMath args.
    @Test
    public void buildStringPatternTest() {

        // Daily, weekly, monthly, quarterly and yearly in order of initialization.
        String[] expectedPatterns = {"now-1d/d", "now-1d/d-1w/d", "now-1d/d-1M/d", "now-1d/d-3M/d", "now-1d/d-1y/d", "now/d"};

        dailyGte = dateMathStringBuilder.buildStringPattern(NOW, MINUS, ONE_DAY, RBTS_OF, DAY);
        weeklyGte = dateMathStringBuilder.buildStringPattern(NOW, MINUS,ONE_DAY,RBTS_OF,DAY,MINUS,ONE_WEEK,RBTS_OF,DAY);
        monthlyGte = dateMathStringBuilder.buildStringPattern(NOW, MINUS, ONE_DAY, RBTS_OF, DAY, MINUS, ONE_MONTH, RBTS_OF, DAY);
        quarterlyGte = dateMathStringBuilder.buildStringPattern(NOW, MINUS, ONE_DAY, RBTS_OF, DAY, MINUS, THREE_MONTHS, RBTS_OF, DAY);
        yearlyGte = dateMathStringBuilder.buildStringPattern(NOW,MINUS,ONE_DAY,RBTS_OF,DAY,MINUS,ONE_YEAR,RBTS_OF,DAY);
        lt = dateMathStringBuilder.buildStringPattern(NOW,RBTS_OF,DAY);

        Map<String, String> testingMap = new HashMap<>();
        testingMap.put(dailyGte, expectedPatterns[0]);
        testingMap.put(weeklyGte, expectedPatterns[1]);
        testingMap.put(monthlyGte, expectedPatterns[2]);
        testingMap.put(quarterlyGte, expectedPatterns[3]);
        testingMap.put(yearlyGte, expectedPatterns[4]);
        testingMap.put(lt, expectedPatterns[5]);

        testingMap.forEach( (k,v) -> {
            assertTrue(k.equals(v));
        });

    }
}