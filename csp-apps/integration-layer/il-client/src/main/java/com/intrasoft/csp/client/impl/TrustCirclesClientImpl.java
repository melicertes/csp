package com.intrasoft.csp.client.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by iskitsas on 4/8/17.
 */
public class TrustCirclesClientImpl implements TrustCirclesClient {
    String context;
    private Logger LOG = (Logger) LoggerFactory.getLogger(TrustCirclesClientImpl.class);

    @Autowired
    @Qualifier("TcRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    TrustCirclesClientConfig config;


    @PostConstruct
    public void init(){
        context = config.getTcBaseContext();
    }

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }


    @Override
    public List<TrustCircle> getAllTrustCircles() {
        String url = context + config.getTcPathCircles();
        LOG.debug("API call [post]: " + url);
        List<TrustCircle> list = Arrays.asList(retryRestTemplate.getForObject(url, TrustCircle[].class));
        return list;
    }

    @Override
    public List<Team> getAllTeams() {
        String url = context + config.getTcPathTeams();
        LOG.debug("API call [post]: " + url);
        List<Team> list = Arrays.asList(retryRestTemplate.getForObject(url, Team[].class));
        return list;
    }

    @Override
    public TrustCircle getTrustCircleByUuid(String uuid) {
        String url = context + config.getTcPathCircles() +"/"+uuid;
        LOG.debug("API call [post]: " + url);
        TrustCircle trustCircle = retryRestTemplate.getForObject(url, TrustCircle.class);
        return trustCircle;
    }

    @Override
    public Team getTeamByUuid(String uuid) {
        String url = context + config.getTcPathTeams() +"/"+uuid;
        LOG.debug("API call [post]: " + url);
        Team team = retryRestTemplate.getForObject(url, Team.class);
        return team;
    }

    @Override
    public String getContext() {
        return context;
    }

}
