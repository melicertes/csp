package com.intrasoft.csp.regrep.service;

import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.LogstashMappingType;

public interface RequestBodyService {

    String requestBodyBuilder(DateMath gte, DateMath lt, LogstashMappingType type);

    String requestBodyBuilder(DateMath gte, DateMath lt, CspDataMappingType type);

}
