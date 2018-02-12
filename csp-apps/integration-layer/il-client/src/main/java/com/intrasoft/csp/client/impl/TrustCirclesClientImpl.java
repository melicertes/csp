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


    String baseContextPath;
    String pathCircles;
    String pathTeams;
    String pathLocalCircle;

    public TrustCirclesClientImpl(String baseContextPath, String pathCircles, String pathTeams, String pathLocalCircle) {
        this.baseContextPath = baseContextPath;
        this.pathCircles = pathCircles;
        this.pathTeams = pathTeams;
        this.pathLocalCircle = pathLocalCircle;
    }

    @PostConstruct
    public void init(){
        context = baseContextPath;
    }

    @Override
    public void setProtocolHostPort(String protocol, String host, String port) {
        context = protocol+"://"+host+":"+port;
    }


    @Override
    public List<TrustCircle> getAllTrustCircles() {
        String url = context + pathCircles;
        LOG.debug("API call [post]: " + url);
        List<TrustCircle> list = Arrays.asList(retryRestTemplate.getForObject(url, TrustCircle[].class));
        return list;
    }

    @Override
    public List<Team> getAllTeams() {
        String url = context + pathTeams;
        LOG.debug("API call [post]: " + url);
        List<Team> list = Arrays.asList(retryRestTemplate.getForObject(url, Team[].class));
        return list;
    }

    @Override
    public TrustCircle getTrustCircleByUuid(String uuid) {
        String url = context + pathCircles +"/"+uuid;
        LOG.debug("API call [post]: " + url);
        TrustCircle trustCircle = retryRestTemplate.getForObject(url, TrustCircle.class);
        return trustCircle;
    }

    @Override
    public Team getTeamByUuid(String uuid) {
        String url = context + pathTeams +"/"+uuid;
        LOG.debug("API call [post]: " + url);
        Team team = retryRestTemplate.getForObject(url, Team.class);
        return team;
    }

    @Override
    public List<TrustCircle> getAllLocalTrustCircles() {
        String url = context + pathLocalCircle;
        LOG.debug("API call [get]: " + url);
        List<TrustCircle> list = Arrays.asList(retryRestTemplate.getForObject(url, TrustCircle[].class));
        return list;
    }

    @Override
    public TrustCircle getLocalTrustCircleByUuid(String uuid) {
        String url = context + pathLocalCircle +"/"+uuid;
        LOG.debug("API call [get]: " + url);
        TrustCircle trustCircle = retryRestTemplate.getForObject(url, TrustCircle.class);
        return trustCircle;
    }

    @Override
    public String getContext() {
        return context;
    }

}
