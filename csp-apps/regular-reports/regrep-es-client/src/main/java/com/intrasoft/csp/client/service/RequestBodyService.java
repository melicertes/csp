package com.intrasoft.csp.client.service;

import com.intrasoft.csp.client.DateMath;
import com.intrasoft.csp.regrep.commons.model.query.ElasticQuery;

public interface RequestBodyService {

    ElasticQuery constructQuery(DateMath gte, DateMath lt);

}
