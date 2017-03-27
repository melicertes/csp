package com.sastix.csp.server.routes;

import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.server.processors.ElasticsearchProcessor;
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
    private ElasticsearchProcessor ddlProcessor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).process(exceptionProcessor)
                .log("[Exception thrown]... Received body ${body}")
                .handled(true);

        from("direct:apps")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));


        from("direct:ddl")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .to("http://localhost:{{server.port}}/es");

        from("direct:es")
                .process(ddlProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(simple("{{elastic.uri}}/${header.esURI}"))
                .log("[ElasticSearch response]... Received response ${body}") ;

    }
}
