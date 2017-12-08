package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MispTcSyncServiceImpl implements MispTcSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(MispTcSyncServiceImpl.class);

    private Map<String, String> teamsToOrgs;
    private Map<String, String> tcsToSgs;

    @Autowired
    TrustCirclesClient trustCirclesClient;

    @Autowired
    MispAppClient mispAppClient;

    @Value("${misp.sync.fixed.delay}")
    Long fixedDelay;

    @Value("${misp.sync.initial.delay}")
    Long initialDelay;

//  TODO: Investigate which additional fields can be mapped
//  TODO: Try using and keeping only OrganisationDTO and SharingGroupDTO instead of the generated classes before pushing!
//  Questions:
//  TODO: If a trust circle/sharing group only exists in MISP should it be deleted?

    private void setup() {

    }

    @Scheduled(fixedDelayString = "${misp.sync.fixed.delay}", initialDelayString = "${misp.sync.initial.delay}")
    @Override
    public void sync() {

        // It would be wise to have organisations synchronized first, before synchronizing sharing groups.
        syncOrganisations();
        syncSharingGroups();

    }

    private void syncOrganisations() {
        List<Team> teamList = trustCirclesClient.getAllTeams();
        List<OrganisationDTO> orgList = mispAppClient.getAllMispOrganisations();
        OrganisationDTO organisation = null;

        // Algorithm that makes the necessary Organisation API calls to MISP, depending on TC's Teams content.
        for (int i=0; i<teamList.size(); i++) {
            for (int j=0; j<orgList.size(); j++) {
                organisation = orgList.get(j);
                // Match
                if (teamList.get(i).getId().equals(organisation.getUuid())) {
                    // Populating this MISP organisation with this TC team's data and updating MISP.
                    mapTeamToOrg(teamList.get(i), organisation);
                    mispAppClient.updateMispOrganisation(organisation);
                    break;
                }
            }
            // No match; create this team as an organisation in MISP.
            mapTeamToOrg(teamList.get(i), organisation);
            mispAppClient.addMispOrganisation(organisation);
        }

        // Delete orphan MISP organisations

        // Refreshing our list first
        orgList = mispAppClient.getAllMispOrganisations();
        List<String> teamIdList = new ArrayList<>();

        // Getting orphan MISP organisation ids
        for (OrganisationDTO org : orgList) {
            for (Team team : teamList) {
                if (org.getUuid().equals(team.getId()))
                    break;
            }
            teamIdList.add(org.getId());
        }

        LOG.info("Found " + teamIdList.size() + " orphan organisations in MISP");
        teamIdList.forEach(id -> mispAppClient.deleteMispOrganisation(id));

    }

    private void syncSharingGroups() {

        List<TrustCircle> tcList = trustCirclesClient.getAllTrustCircles();
        List<SharingGroup> sgList = mispAppClient.getAllMispSharingGroups();
        SharingGroup sharingGroup = null;

//      TODO: Currently Sharing Groups don't have a uuid field but we'll be treating them like they do.

        for (int i=0; i<tcList.size(); i++) {
            for (int j=0; j<sgList.size(); j++) {
                sharingGroup = sgList.get(j);
                // Match
                if (tcList.get(i).getId().equals(sharingGroup.getUuid())) {
                    // Populating this MISP sharing group with the matching TC trust circle data and updating MISP.
                    // todo: map trust circle to sharing group
                    // todo: update misp
                    break;
                }
            }
            // No match; create this Trust Circle as a Sharing Group in MISP.

        }

    }


    private void mapTeamToOrg(Team team, OrganisationDTO organisation) {

        organisation.setUuid(team.getId());
        organisation.setName(team.getName());
        organisation.setDescription(team.getDescription());
        organisation.setNationality(team.getCountry());

    }

    private void mapTcToSg(TrustCircle tc, SharingGroup sg) {

        sg.setUuid(tc.getId());
        sg.setName(tc.getName());
        sg.setDescription(tc.getDescription());

        // Organisations list and generated class mappings
        

    }



}
