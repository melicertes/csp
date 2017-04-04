package com.sastix.csp.server.routes;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.server.processors.DdlProcessor;
import com.sastix.csp.server.processors.DclProcessor;
import com.sastix.csp.server.processors.EDclProcessor;
import com.sastix.csp.server.processors.ExceptionProcessor;
import com.sastix.csp.server.processors.RecipientsProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DSLRoute extends RouteBuilder {

    @Autowired
    private ExceptionProcessor exceptionProcessor;

    @Autowired
    private RecipientsProcessor recipientsProcessor;

    @Autowired
    private DdlProcessor ddlProcessor;

    @Autowired
    private DclProcessor dclProcessor;

    @Autowired
    private EDclProcessor edclProcessor;

    @Override
    public void configure() {

//        onException(Exception.class).process(exceptionProcessor)
//                .log("[Exception thrown]... Received body ${body}")
//                .handled(true);


        from("direct:dsl")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));
                //.to("direct:dcl");


        from("direct:ddl")
                .process(ddlProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"))
                //.to("http://localhost:8081/test")
                //.recipientList(simple("${header.elastic}"))
                //.recipientList(simple("${header.esURI}"))
                .log("[ElasticSearch response]... Received response ${body}");


        from("direct:dcl")
                .process(dclProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                //.marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("ecsps"));


        from("direct:edcl")
                .process(edclProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .to("direct:dsl");


    }
}
