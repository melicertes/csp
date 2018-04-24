package com.intrasoft.csp.regrep.service;

import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.LogstashMappingType;

public interface RequestBodyService {

    String buildRequestBody(DateMath gte, DateMath lt, LogstashMappingType type);

    String buildRequestBody(DateMath gte, DateMath lt, CspDataMappingType type);

    String buildRequestBodyForLogs(DateMath gte, DateMath lt, LogstashMappingType type);

}
