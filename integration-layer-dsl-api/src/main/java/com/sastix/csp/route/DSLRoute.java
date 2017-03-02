package com.sastix.csp.route;

import com.sastix.csp.model.IntegrationData;
import com.sastix.csp.processor.ExceptionProcessor;
import com.sastix.csp.processor.RecipientsProcessor;
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

    @Override
    public void configure() throws Exception {

        onException(Exception.class).process(exceptionProcessor)
                .log("[Exception thrown]... Received body ${body}")
                .handled(true);

        from("direct:vulnerability")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:artefact")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:threat")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:incident")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:file")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:contact")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:chat")
                .process(recipientsProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .recipientList(header("recipients"));

        from("direct:ddl")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson, IntegrationData.class)
                .to("http://localhost:{{server.port}}/ddl");
    }
}
