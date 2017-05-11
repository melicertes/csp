package com.sastix.csp.commons.routes;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by iskitsas on 4/7/17.
 */
public interface CamelRoutes {
    String DIRECT ="direct";
    String ACTIVEMQ ="activemq";
    String ACTIVEMQ_TX ="activemqtx";
    String MOCK_PREFIX ="mock";

    String DSL ="dsl";
    String DCL ="dcl";
    String EDCL ="edcl";
    String DDL ="ddl";
    String TC ="tc";
    String TCT ="tct";
    String ECSP ="ecsp";
    String APP ="app";
    String ELASTIC ="elastic";

    String ORIGIN_ENDPOINT ="originEndpoint";
    String ERROR ="error";
//String DSL ="direct:dsl";
//    String DCL ="direct:dcl";
//    String EDCL ="direct:edcl";
//    String DDL ="direct:ddl";
//    String TC ="direct:tc";
//    String TCT ="direct:tct";
//    String ECSP ="direct:ecsp";
//    String APP ="direct:app";
//    String ELASTIC ="direct:elastic";
}
