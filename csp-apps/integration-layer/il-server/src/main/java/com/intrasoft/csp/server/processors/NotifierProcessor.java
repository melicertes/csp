package com.intrasoft.csp.server.processors;

import com.intrasoft.csp.commons.model.EnhancedTeamDTO;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.config.CspSslConfiguration;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intrasoft.csp.commons.routes.CamelRoutes.ECSP;

@Component
public class NotifierProcessor implements Processor {
    private static final Logger LOG = LoggerFactory.getLogger(NotifierProcessor.class);
    private static final int TIMEOUT = 5000;

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
        checkAndDeliver(team);
    }

    @Scheduled(initialDelay = 60000L, fixedDelay = 60000000L)
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
                failedConnectivityTest.remove(t);
                LOG.debug("Notifier - RETRY - CSP {} ({}) {}", t.getCspId(), t.getUrl(), t.getCountry());
                checkAndDeliver(t);
            });

        }
    }

    private void checkAndDeliver(Team team) {
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
            consumeMessagesFrom(team);
        } else { // we have failed the connectivity test.
            failedConnectivityTest.put(team, LocalDateTime.now());
        }
    }

    private void consumeMessagesFrom(Team team) {
        Exchange exchange = null;
        while ( (exchange = consumer.receive(routes.apply(ECSP+"."+team.getName()), TIMEOUT)) != null) {
            final Map<String, Object> headers = exchange.getIn().getHeaders();
            EnhancedTeamDTO body = exchange.getIn().getBody(EnhancedTeamDTO.class);
            LOG.info("Notifier - Delivering type {} to {}/{}", body.getIntegrationData().getDataType(), team.getCspId(),team.getUrl());
            producer.sendBodyAndHeaders(routes.apply(ECSP),
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
}
