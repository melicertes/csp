package com.intrasoft.csp.server.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.EnhancedTeamDTO;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.config.CspSslConfiguration;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.apache.camel.util.GZIPHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intrasoft.csp.commons.routes.CamelRoutes.ECSP;
import static com.intrasoft.csp.commons.routes.CamelRoutes.FILE_DUMP_GLOBAL;

@Component
public class NotifierProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(NotifierProcessor.class);
    private static final int TIMEOUT = 5000;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${server.subdomain.prefix}")
    String serverSubdomainPrefix;

    @Autowired
    CamelRestService camelRestService;

    @Autowired
    RouteUtils routes;

    @Autowired
    ConsumerTemplate consumer;

    @EndpointInject
    ProducerTemplate producer;

    @Autowired
    CspSslConfiguration cspSslConfiguration;

    @Value("${il.notifier.reprocess.failed.minutes:30}")
    Integer retryFailedMinutes;

    Map<Team, LocalDateTime> failedConnectivityTest = new HashMap<>();

    @Override
    public void process(Exchange exchange) throws Exception {
        Team team = exchange.getIn().getBody(Team.class);
        checkAndDeliver(team, LocalDateTime.now()); //we check against the event just received (assume failure is now)
    }

    /**
     * scheduler runs ever 30m
     */
    @Scheduled(initialDelay = 60000L, fixedDelay = 1800000L)
    public void scheduledRetryConnectivity() {
        if (failedConnectivityTest.size() > 0) {
            final Map<Team, LocalDateTime> copied = new HashMap<>(failedConnectivityTest);
            final List<Team> forProcessing =
                copied.entrySet()
                    .stream()
                    .filter( e -> e.getValue().plus(retryFailedMinutes, ChronoUnit.MINUTES).isBefore(LocalDateTime.now()))
                    .map( entry -> entry.getKey()).collect(Collectors.toList()); // these are expired entries, we need to reprocess.
            LOG.debug("Notifier - Now will retry the following CSP teams: {}", copied.size());
            forProcessing.forEach( t -> {
                LocalDateTime timeOfFailure = failedConnectivityTest.get(t); //get the last timeOfFailure
                LOG.debug("Notifier - RETRY - CSP {} ({}) {}", t.getCspId(), t.getUrl(), t.getCountry());
                checkAndDeliver(t, timeOfFailure);
            });

        }
    }

    private void checkAndDeliver(Team team, LocalDateTime timeOfFailure) {
        LOG.info("Notifier - Testing connectivity to external CSP: " + team.getName() + " -- " + team.getUrl());
        String externalSslPort = StringUtils.isEmpty(cspSslConfiguration.getExternalSslPort())?"":
                ":"+cspSslConfiguration.getExternalSslPort();

        String api = team.getUrl()+externalSslPort + "/" + ContextUrl.GET_API_VERSION;
        //external certificate
        if(cspSslConfiguration.getExternalUseSSL()){
            if(api.contains("http")) {
                api = api.replaceAll("http", cspSslConfiguration.getExternalSslEndpointProtocol());
            }else{
                api = cspSslConfiguration.getExternalSslEndpointProtocol()+"://"
                        +(!StringUtils.isEmpty(serverSubdomainPrefix)?serverSubdomainPrefix+".":"")
                        +api;
            }
        }

        LOG.debug("Notifier - checking URI resolved: {}" , api );
        // check connectivity here
        if (checkConnectivity(api)) {
            LOG.debug("Notifier - Connectivity success to {} - now delivering messages",api);
            deliverMessagesTo(team);
            failedConnectivityTest.remove(team); //remove from rechecking list
        } else { // we have failed the connectivity test.
            if (!failedConnectivityTest.containsKey(team)) {
                failedConnectivityTest.put(team, timeOfFailure);
                LOG.warn("Notifier - Connectivity has failed for {} at {}",team, api);
            } else {
                final LocalDateTime failureTime = failedConnectivityTest.get(team);
                LOG.warn("Notifier - Connectivity has failed again for {}, failing since {}", team, failureTime);
                if (failureTime.plusWeeks(1).isBefore(LocalDateTime.now())) { // failing for more than 1 week
                    LOG.error("Notifier - team {} fails delivery for more than 1 week, expiring all messages",team);
                    Exchange exchange = null;
                    while ((exchange = consumer.receive(routes.wrap(ECSP + "." + routes.safeQueueName(team)), TIMEOUT)) != null) {
                        final Map<String, Object> headers = exchange.getIn().getHeaders();
                        EnhancedTeamDTO body = exchange.getIn().getBody(EnhancedTeamDTO.class);
                        producer.sendBody(FILE_DUMP_GLOBAL, convertToDump(headers,body));
                    }
                    failedConnectivityTest.remove(team); //remove from rechecking list as we've now emptied the queues
                }
            }
        }
    }
    private void deliverMessagesTo(Team team) {
        Exchange exchange = null;
        while ( (exchange = consumer.receive(routes.wrap(ECSP+"."+routes.safeQueueName(team)), TIMEOUT)) != null) {
            final Map<String, Object> headers = exchange.getIn().getHeaders();
            EnhancedTeamDTO body = exchange.getIn().getBody(EnhancedTeamDTO.class);
            LOG.info("Notifier - Delivering type {} to {}/{}", body.getIntegrationData().getDataType(), team.getCspId(),team.getUrl());
            producer.sendBodyAndHeaders(routes.wrap(ECSP),
                    ExchangePattern.InOnly, body, headers);
        }
    }

    private boolean checkConnectivity(String api) {
        try {
            final String s = camelRestService.send(api, null, "GET");
            LOG.debug("Notifier - check to {} returned {} - will try to connect", api, s);
            return true;
        } catch (Exception e) {
            LOG.error("Notifier - check to {} returned error {} - check has failed", api, e.getMessage());
            return false;
        }
    }


    /**
     * convert the message to a more compact format to dump in the logs
     * @param headers
     * @param body
     * @return a simple String payload
     */
    private String convertToDump(Map<String, Object> headers, EnhancedTeamDTO body) {
        try {
            final byte[] gzip = GZIPHelper.compressGZIP(objectMapper.writeValueAsBytes(body));
            return String.format("{ \"dmp\":\"%s\" }", Base64.getEncoder().encodeToString(gzip));
        } catch (IOException e) {
            LOG.error("While packaging dump of {}, error occurred {} ",body.getTeam().getName(), e.getMessage());
            return String.format("{ \"dmp\":\"event dump for team %s failed to serialize - %s - %s\" }", body.getTeam().getName(), e.getMessage(), body.getIntegrationData().getDataType());
        }
    }


}
