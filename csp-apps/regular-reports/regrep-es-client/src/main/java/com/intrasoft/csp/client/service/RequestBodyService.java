package com.intrasoft.csp.client.service;

import com.intrasoft.csp.client.CspDataMappingType;
import com.intrasoft.csp.client.DateMath;
import com.intrasoft.csp.client.LogstashMappingType;

public interface RequestBodyService {

    String requestBodyBuilder(DateMath gte, DateMath lt, LogstashMappingType type);

    String requestBodyBuilder(DateMath gte, DateMath lt, CspDataMappingType type);

}
