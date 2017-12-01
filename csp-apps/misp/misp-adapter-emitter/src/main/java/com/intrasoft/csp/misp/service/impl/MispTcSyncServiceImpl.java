package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MispTcSyncServiceImpl implements MispTcSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(MispTcSyncServiceImpl.class);

    @Autowired
    TrustCirclesClient trustCirclesClient;

    @Autowired
    MispAppClient mispAppClient;


    private void syncOrganisations() {
//        TODO: Also modify the organisation type field to something like "CspOrg".
    }

    private void syncSharingGroups() {


    }

    @Scheduled(fixedDelayString = "${misp.sync.fixed.delay}", initialDelayString = "${misp.sync.initial.delay}")
    @Override
    public void sync() {

    }
}
