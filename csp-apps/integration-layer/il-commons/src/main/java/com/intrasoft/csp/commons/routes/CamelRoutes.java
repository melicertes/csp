package com.intrasoft.csp.commons.routes;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by iskitsas on 4/7/17.
 */
public interface CamelRoutes {
    String VM ="vm";
    String DIRECT ="vm";
    String ACTIVEMQ ="activemq";
    String ACTIVEMQ_TX ="activemqtx";
    String MOCK_PREFIX ="mock";

    String DSL ="dsl";
    String DCL ="dcl";
    String EDCL ="edcl";
    String DDL ="ddl";
    String TC ="tc";
    String ECSP ="ecsp";
    String APP ="app";
    String ELASTIC ="elastic";

    String ORIGIN_ENDPOINT ="originEndpoint";
    String ERROR ="error";
}
