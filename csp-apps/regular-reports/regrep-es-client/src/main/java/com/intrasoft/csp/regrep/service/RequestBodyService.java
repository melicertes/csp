package com.intrasoft.csp.regrep.service;

import com.intrasoft.csp.regrep.CspDataMappingType;
import com.intrasoft.csp.regrep.DateMath;
import com.intrasoft.csp.regrep.LogstashMappingType;

public interface RequestBodyService {

    String buildRequestBody(String gte, String lt, LogstashMappingType type);

    String buildRequestBody(String gte, String lt, CspDataMappingType type);

    String buildRequestBodyForLogs(String gte, String lt, LogstashMappingType type);

}
