package com.sastix.csp.server.routes;

import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.processors.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DSLRoute extends RouteBuilder {

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


    @Override
    public void configure() {

//        onException(Exception.class).process(exceptionProcessor)
//                .log("[Exception thrown]... Received body ${body}")
//                .handled(true);


        from(CamelRoutes.DSL)
                .process(dslProcessor)
                .recipientList(header("recipients"));

        from(CamelRoutes.DDL)
                .process(ddlProcessor)
                .recipientList(header("recipients"));

        from(CamelRoutes.DCL)
                .process(dclProcessor)
                .recipientList(header("recipients"));

        from(CamelRoutes.EDCL)
                .process(edclProcessor);
//                .to(CamelRoutes.DSL);

        //TrustCircles Circles routes
        from(CamelRoutes.TC)
                .process(tcProcessor)
                .marshal().json(JsonLibrary.Jackson, Csp.class);

        //TrustCircles Teams routes
        from(CamelRoutes.TCT)
                .process(teamProcessor)
                .marshal().json(JsonLibrary.Jackson, Csp.class)
                .recipientList(header("recipients"));

        //ExternalCSPs
        from(CamelRoutes.ECSP)
                .process(ecspProcessor);


        //App routing
        from(CamelRoutes.APP)
                .process(appProcessor);

        //Elastic route
        from(CamelRoutes.ELASTIC)
                .process(elasticProcessor);

    }
}
