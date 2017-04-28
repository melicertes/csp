package com.sastix.csp.server.routes;

import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.processors.*;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CspRoutes extends RouteBuilder implements CamelRoutes{

    @Autowired
    private ExceptionProcessor exceptionProcessor;

    @Autowired
    private DslProcessor dslProcessor;

    @Autowired
    private DdlProcessor ddlProcessor;

    @Autowired
    private DclProcessor dclProcessor;

    @Autowired
    private EdclProcessor edclProcessor;

    @Autowired
    private TcProcessor tcProcessor;

    @Autowired
    private TeamProcessor teamProcessor;

    @Autowired
    private EcspProcessor ecspProcessor;

    @Autowired
    private AppProcessor appProcessor;

    @Autowired
    private ElasticProcessor elasticProcessor;

    @Autowired
    RouteUtils endpoint;

    @Value("${activemq.redelivery.delay}")
    private long redeliveryDelay;

    @Value("${activemq.max.redelivery.attempts}")
    private int maxRedeliveryAttempts;


    @Override
    public void configure() {

        //errorHandler(defaultErrorHandler().maximumRedeliveries(2).redeliveryDelay(1000).retryAttemptedLogLevel(LoggingLevel.WARN));

        onException(Exception.class)
                .maximumRedeliveries(maxRedeliveryAttempts)
                .redeliveryDelay(redeliveryDelay)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .process(exceptionProcessor)
                .handled(true)
                //.to(endpoint.apply(ERROR))
        ;

        from(endpoint.apply(DSL))
                .process(dslProcessor)
                .recipientList(header("recipients"));

        from(endpoint.apply(DDL))
                .process(ddlProcessor)
                .recipientList(header("recipients"));

        from(endpoint.apply(DCL))
                .process(dclProcessor)
                .recipientList(header("recipients"));

        from(endpoint.apply(EDCL))
                .process(edclProcessor);
//                .to(CamelRoutes.DSL);

        //TrustCircles Circles routes
        from(endpoint.apply(TC))
                .process(tcProcessor)
                .marshal().json(JsonLibrary.Jackson, Csp.class);

        //TrustCircles Teams routes
        from(endpoint.apply(TCT))
                .process(teamProcessor)
                .marshal().json(JsonLibrary.Jackson, Csp.class)
                .recipientList(header("recipients"));

        //ExternalCSPs
        from(endpoint.apply(ECSP))
                .process(ecspProcessor);


        //App routing
        from(endpoint.apply(APP))
                .process(appProcessor);

        //Elastic route
        from(endpoint.apply(ELASTIC))
                .process(elasticProcessor);

    }
}
