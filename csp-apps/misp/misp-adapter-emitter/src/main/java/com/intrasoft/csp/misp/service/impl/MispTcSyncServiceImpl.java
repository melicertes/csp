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

    private void setup() {

    }

    @Scheduled(fixedDelayString = "${misp.sync.fixed.delay}", initialDelayString = "${misp.sync.initial.delay}")
    @Override
    public void syncAll() {

        // It would be wise to have organisations synchronized first, before synchronizing sharing groups.
        syncOrganisations();
        syncSharingGroups();

    }

//  TODO: Add name validation (name duplicates not allowed)
//  TODO: Organisations can't be deleted when they reference users or are referenced themselves by misp events
//    Team objects with new Uuids also need to have new names (case-sensitive) in order to be created in MISP.
    public void syncOrganisations() {
        List<Team> teamList = trustCirclesClient.getAllTeams();
        List<OrganisationDTO> orgList = mispAppClient.getAllMispOrganisations();
        OrganisationDTO organisation = null;

        // Making the necessary calls to MISP, depending on TC's Teams content.
        boolean loopBreak;
        for (int i=0; i<teamList.size(); i++) {
            loopBreak = false;
            for (int j=0; j<orgList.size(); j++) {
                organisation = orgList.get(j);
                // Uuid Match
                if (teamList.get(i).getId().equals(organisation.getUuid())) {
                    // Updating the MISP organisation with the TC team data.
                    mapTeamToOrganisation(teamList.get(i), organisation);
                    mispAppClient.updateMispOrganisation(organisation);
                    loopBreak = true;
                    break;
                // Name match
                } else if (teamList.get(i).getName().equals(organisation.getName())){
//                    TODO: What should the default behavior be? Update the organisation when found only by name?
                    LOG.info("*** Found the same organisation name with a different UUID; updating to match TC... "
                            + organisation.getUuid());
                    mapTeamToOrganisation(teamList.get(i), organisation);
                    mispAppClient.updateMispOrganisation(organisation);
                    loopBreak = true;
                    break;
                }
            }
            if (loopBreak) continue;
            // No match; create this team as an organisation in MISP.
            OrganisationDTO newOrg = new OrganisationDTO();
            mapTeamToOrganisation(teamList.get(i), newOrg);
            mispAppClient.addMispOrganisation(newOrg);
        }

        // Delete any MISP Organisations that don't exist in Trust Circles.
        // (Organisation references are removed automatically from any sharing groups when deleted. Tested in MISP UI)

        // Refreshing our lists first
        orgList = mispAppClient.getAllMispOrganisations();
//        teamList = trustCirclesClient.getAllTeams();
        List<String> teamIdList = new ArrayList<>();

        // Getting orphan MISP organisation ids
        for (OrganisationDTO org : orgList) {
            loopBreak = false;
            for (Team team : teamList) {
                if (org.getUuid().equals(team.getId())) {
                    loopBreak = true;
                    break;
                }
            }
            if (loopBreak) continue;
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
        organisation.setLocal(true); // What's the deal with this?

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
