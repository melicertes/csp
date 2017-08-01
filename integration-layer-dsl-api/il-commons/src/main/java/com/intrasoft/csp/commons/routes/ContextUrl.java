package com.intrasoft.csp.commons.routes;

/**
 * Created by iskitsas on 4/4/17.
 */
public interface ContextUrl {
    String DSL_INTEGRATION_DATA= "/dsl/integrationData";
    String DCL_INTEGRATION_DATA= "/dcl/integrationData";
    String ADAPTER_INTEGRATION_DATA= "/adapter/integrationData";

    String MANAGE_CONSUME_ERROR_Q= "/manage/consume/errorq";

    String GET_API_VERSION = "apiversion";

    /**
     * REST API versions supported.
     * double values, eg 1, 1.1,1.2 etc
     * */
    String REST_API_V1 = "1";
}
