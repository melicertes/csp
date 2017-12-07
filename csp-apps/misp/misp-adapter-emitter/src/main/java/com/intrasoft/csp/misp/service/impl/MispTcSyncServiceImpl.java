package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.client.config.TrustCirclesClientConfig;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.Organisation;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MispTcSyncServiceImpl implements MispTcSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(MispTcSyncServiceImpl.class);

    @Autowired
    TrustCirclesClient trustCirclesClient;

    @Autowired
    MispAppClient mispAppClient;

//  TODO: See what fields can be mapped
//  TODO: Create a mapping service using a data structure of Map<teamField:String,orgField:String>
//  TODO: Change Response class name to something like a wrapper because it's misguiding
//  TODO: Try using and keeping only OrganisationDTO instead of the generated Organisation class.
//  Questions:
//  TODO: If a team/organisation only exists in MISP should it be deleted?
//  TODO: If a trust circle/sharing group only exists in MISP should it be deleted?

    private void syncOrganisations() {
        List<Team> teamList = trustCirclesClient.getAllTeams();
//        List<OrganisationDTO> orgList = mispAppClient.get

        // Get all teams from Trust Circles
        // Get all organisations from MISP

        // for each team in Trust Circles (pay attention to efficiency and speed when implementing algorithm)
            // for each organisation in MISP
                // if team.uuid matches org.uuid
                    // UPDATE matching organisation in MISP with team data (check/investigate if there are any implications)
                    // break loop (skip next organisations if any and resume team iteration)
            // next organisation
            // (this line of execution means current team doesn't exist in MISP)
            // Create the current team as organisation in MISP (create an interface/service or do the mapping here?)
        // next team

/*
        for (int i=0; i<teamList.size(); i++) {
            for (int i=0; i<team)
        }
*/




    }

    private void syncSharingGroups() {

    }

    @Scheduled(fixedDelayString = "${misp.sync.fixed.delay}", initialDelayString = "${misp.sync.initial.delay}")
    @Override
    public void sync() {

        // It would be wise to have organisations synchronized first, before synchronizing sharing groups.
        syncOrganisations();

    }


}
