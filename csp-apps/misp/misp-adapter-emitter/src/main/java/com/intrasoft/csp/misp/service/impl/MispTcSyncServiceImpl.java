package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroupOrgItem;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MispTcSyncServiceImpl implements MispTcSyncService {

    private static final Logger LOG = LoggerFactory.getLogger(MispTcSyncServiceImpl.class);

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
    public void syncAll() {

        // It would be wise to have organisations synchronized first, before synchronizing sharing groups.
        syncOrganisations();
        syncSharingGroups();

    }


    public void syncOrganisations() {
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
                    mapTeamToOrganisation(teamList.get(i), organisation);
                    mispAppClient.updateMispOrganisation(organisation);
                    break;
                }
            }
            // No match; create this team as an organisation in MISP.
            mapTeamToOrganisation(teamList.get(i), organisation);
            mispAppClient.addMispOrganisation(organisation);
        }

        // Delete orphan MISP organisations procedure
        // Deleted organisations are removed automatically from any sharing groups when deleted. Tested in MISP UI.

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

    public void syncSharingGroups() {

        List<TrustCircle> tcList = trustCirclesClient.getAllTrustCircles();
        List<SharingGroup> sgList = mispAppClient.getAllMispSharingGroups();
        SharingGroup sharingGroup = null;

        for (int i=0; i<tcList.size(); i++) {
            for (int j=0; j<sgList.size(); j++) {
                sharingGroup = sgList.get(j);
                // Match
                if (tcList.get(i).getId().equals(sharingGroup.getUuid())) {
                    // Populating this MISP sharing group with the matching TC trust circle data and updating MISP.
                    mapTrustCircleToSharingGroup(tcList.get(i), sharingGroup);
                    mispAppClient.updateMispSharingGroup(sharingGroup);
                    break;
                }
            }
            // No match; create this Trust Circle as a Sharing Group in MISP.
            sharingGroup = new SharingGroup();
            mapTrustCircleToSharingGroup(tcList.get(i), sharingGroup);
            mispAppClient.addMispSharingGroup(sharingGroup);
        }

    }

    private void mapTeamToOrganisation(Team team, OrganisationDTO organisation) {

        organisation.setUuid(team.getId());
        organisation.setName(team.getName());
        organisation.setDescription(team.getDescription());
        organisation.setNationality(team.getCountry());

    }

    private void mapTrustCircleToSharingGroup(TrustCircle tCircle, SharingGroup sGroup) {

        sGroup.setUuid(tCircle.getId());
        sGroup.setName(tCircle.getName());
        sGroup.setDescription(tCircle.getDescription());

        List<String> tCircleTeamsUuids = tCircle.getTeams();
        List<SharingGroupOrgItem>  sharingGroupOrgItemList = sGroup.getSharingGroupOrg();

        Map<String, Boolean> sGroupOrgCheckmap = new HashMap<>();

        // Organisations in Sharing Groups have meta data and they're actually part of another object, SharingGroupOrg
        tCircleTeamsUuids.forEach( uuid -> {
            sharingGroupOrgItemList.forEach(sgoi -> {
                // If the uuid is found, just update the hashmap in order to know what team is already there
                if (uuid.equals(sgoi.getOrganisation().getUuid()))
                    sGroupOrgCheckmap.put(uuid,true);
            });
        });

        // For any false value in the map, get the corresponding keys' organisations in MISP and add it in the sharing
        // group's list of organisations (list of SharingGroupOrgItems).
        sGroupOrgCheckmap.forEach((k,v)-> {
            if (v == false) {
                // Sharing Group method for adding a sharingGroupOrgItem which contains an organisation with this uuid.
                SharingGroupOrgItem newSgOrgItem = new SharingGroupOrgItem();
                // There should be an organisation from MISP with this ID fetch it and add it.
                OrganisationDTO newOrg = new OrganisationDTO();
                newOrg = mispAppClient.getMispOrganisation(k);
                newSgOrgItem.setOrganisation(newOrg);
                sGroup.addSharingGroupOrgItem(newSgOrgItem);
            }
        });

    }

}
