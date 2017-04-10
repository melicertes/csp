package com.sastix.csp.server.routes;

import com.sastix.csp.commons.model.Csp;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.server.processors.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExpressionBuilder;
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
    private EDclProcessor edclProcessor;

    @Autowired
    private TcProcessor tcProcessor;

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
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));
                //.to("direct:dcl");


        from(CamelRoutes.DDL)
                .process(ddlProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"))
                //.to("http://localhost:8081/test")
                //.recipientList(simple("${header.elastic}"))
                //.recipientList(simple("${header.esURI}"))
                .log("[ElasticSearch response]... Received response ${body}");


        from(CamelRoutes.DCL)
                .process(dclProcessor)
                //.setHeader(Exchange.HTTP_METHOD, constant("POST"))
                //.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                //.recipientList(header("ecsps"))
        ;


        from(CamelRoutes.EDCL)
                .process(edclProcessor)
//                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
//                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
//                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .to(CamelRoutes.DSL);

        //TrustCircles routes
        from(CamelRoutes.TC)
                .process(tcProcessor)
                .marshal().json(JsonLibrary.Jackson, Csp.class);

        //ExternalCSPs
        from(CamelRoutes.ECSP)
                .process(ecspProcessor)
                .marshal().json(JsonLibrary.Jackson, Csp.class)
                .recipientList(header("ecsps"));

        //App routing
        from(CamelRoutes.APP)
                .process(appProcessor);

        //Elastic route
        from(CamelRoutes.ELASTIC)
                .process(elasticProcessor);

    }
}
