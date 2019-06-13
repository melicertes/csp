package com.intrasoft.csp.server.routes;

import com.intrasoft.csp.commons.exceptions.ErrorLogException;
import com.intrasoft.csp.commons.exceptions.InvalidDataException;
import com.intrasoft.csp.commons.exceptions.InvalidSharingParamsException;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.processors.*;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.util.AsyncProcessorConverterHelper;
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
    private EcspProcessor ecspProcessor;

    @Autowired
    private AppProcessor appProcessor;

    @Autowired
    private ElasticProcessor elasticProcessor;

    @Autowired
    private NotifierProcessor notifierProcessor;

    @Autowired
    RouteUtils endpoint;

    @Value("${activemq.redelivery.delay}")
    private long redeliveryDelay;

    @Value("${activemq.max.redelivery.attempts}")
    private int maxRedeliveryAttempts;


    @Override
    public void configure() {

        // same approach with onException below
        /*errorHandler(deadLetterChannel(endpoint.wrap(ERROR))
                .maximumRedeliveries(maxRedeliveryAttempts)
                .redeliveryDelay(redeliveryDelay)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                //.onRedelivery(exceptionProcessor)
                .onPrepareFailure(exceptionProcessor)
        );*/


        // same approach with errorHandler and DQL above
        onException(Exception.class)
                .maximumRedeliveries(maxRedeliveryAttempts)
                .redeliveryDelay(redeliveryDelay)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .asyncDelayedRedelivery()// this is very important to make the IL platform really asynchronous (eg. when using TC nuke and some CSP nodes are down/inactive)
                .process(AsyncProcessorConverterHelper.convert(exceptionProcessor))
                .handled(true)
                .threads(10)
                .inOnly(endpoint.wrap(ERROR))
        ;

        //just handle the specific exception without any redelivery, just let the exceptionProcessor log the error
        onException(ErrorLogException.class).process(exceptionProcessor).handled(true);
        onException(InvalidSharingParamsException.class).process(exceptionProcessor).handled(true);
        onException(InvalidDataException.class).process(exceptionProcessor).handled(true);

        from(endpoint.wrap(DSL))
                .setExchangePattern(ExchangePattern.InOnly)
                //.threads(10)
                .process(dslProcessor)
                .recipientList(header("recipients"));

        from(endpoint.wrap(DDL))
                .setExchangePattern(ExchangePattern.InOnly)
                .process(ddlProcessor)
                .recipientList(header("recipients"));

        from(endpoint.wrap(DCL))
                .process(dclProcessor)
                .recipientList(header("recipients"));

        from(endpoint.wrap(EDCL))
                .process(edclProcessor)
                .recipientList(header("recipients"));

        //TrustCircles Circles routes
        from(endpoint.wrap(TC))
                 //.threads(10)//TC processing is heavy worker,thus making it threaded // commenting out, the TC requests must not be delivered async, since priority for TC server makes a difference
                .process(tcProcessor)
        ;

        //ExternalCSPs
        from(endpoint.wrap(ECSP))
                .setExchangePattern(ExchangePattern.InOnly)
                .process(AsyncProcessorConverterHelper.convert(ecspProcessor));// AsyncProcessorConverterHelper.convert(
        //Notifier processes events for external CSPs, if connectivity is verified, then proceeds to consume messages

        from(endpoint.wrap(CamelRoutes.NOTIFIER))
                .setExchangePattern(ExchangePattern.InOnly)
                .process(AsyncProcessorConverterHelper.convert(notifierProcessor));

        // Route to dump events in a file when failed delivery
        from(FILE_DUMP_GLOBAL)//note: this url should not be passed through endpoint.wrap()
                .id("file-dump-global-queue")
                .to("file:/opt/csplogs/?fileName=CSP.IL_EVENT_DUMP_${date:now:yyyyMMdd-hh.00}.dmp&fileExist=Append");

        //App routing
        from(endpoint.wrap(APP))
                .process(appProcessor);

        //Elastic route
        from(endpoint.wrap(ELASTIC))
                .setExchangePattern(ExchangePattern.InOnly)
                 //.threads(10) // commenting out, the order of the requests might matter, like in TC requests
                .process(elasticProcessor);

    }
}
