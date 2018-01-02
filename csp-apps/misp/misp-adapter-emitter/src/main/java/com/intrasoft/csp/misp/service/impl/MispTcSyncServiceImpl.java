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

import javax.annotation.PostConstruct;
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

    @Value("${server.name}")
    String cspId;

    @Value("${misp.sync.enabled}")
    Boolean syncEnabled;

    @Value("${misp.sync.fixed.delay}")
    Long fixedDelay;

    @Value("${misp.sync.initial.delay}")
    Long initialDelay;

//  TODO: Investigate which additional fields can be mapped

    @PostConstruct
    private void init() {
        LOG.info(" -- Misp sync "+(syncEnabled?"enabled":"disabled"));
    }

    @Scheduled(fixedDelayString = "${misp.sync.fixed.delay}", initialDelayString = "${misp.sync.initial.delay}")
    @Override
    public void syncAll() {
        if(syncEnabled) {
            LOG.info("Misp sync triggered. Will sync all.");
            // It would be wise to have organisations synchronized first, before synchronizing sharing groups.
            syncOrganisations();
            syncSharingGroups();
        }

    }

//  TODO: Add name validation (name duplicates not allowed)
//    Team objects with new Uuids also need to have unique names (case-sensitive) in order to be created in MISP.
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

        // Finding orphan MISP organisations

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
        // TODO: What is the action to be taken when a MISP organisation does not have a corresponding team in TC? (deletion is not an option for now)
        // teamIdList.forEach(id -> mispAppClient.deleteMispOrganisation(id));

    }

    public void syncSharingGroups() {

        List<TrustCircle> tcList = trustCirclesClient.getAllTrustCircles();
        List<SharingGroup> sgList = mispAppClient.getAllMispSharingGroups();
        SharingGroup sharingGroup = null;

        boolean loopBreak;
        for (int i=0; i<tcList.size(); i++) {
            loopBreak=false;
            for (int j=0; j<sgList.size(); j++) {
                sharingGroup = sgList.get(j);
                // Uuid Match
                if (tcList.get(i).getId().equals(sharingGroup.getUuid())) {
                    // Populating this MISP sharing group with the matching TC trust circle data and updating MISP.
                    mapTrustCircleToSharingGroup(tcList.get(i), sharingGroup);
                    mispAppClient.updateMispSharingGroup(sharingGroup);
                    loopBreak=true;
                    break;
                }
            }
            if (loopBreak) continue;
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
        // SXCSP-420: "The team with the csp-id that is equal to this csp-id should be imported as local org."
        if (team.getCspId().equals(cspId)) // case-sensitivity?
            organisation.setLocal(true);
        }

    private void mapTrustCircleToSharingGroup(TrustCircle tCircle, SharingGroup sGroup) {

        sGroup.setUuid(tCircle.getId());
        sGroup.setName(tCircle.getName());
        sGroup.setDescription(tCircle.getDescription());

        List<String> tCircleTeamsUuids = tCircle.getTeams();
        List<SharingGroupOrgItem> sharingGroupOrgItemList = (sGroup.getSharingGroupOrg() == null) ?
                new ArrayList<>() : sGroup.getSharingGroupOrg();

        Map<String, Boolean> sGroupOrgCheckMap = new HashMap<>();

        // If it's not a new sharing group, check which of the team uuids already exist as orgs in the MISP sh. group.
        if (sharingGroupOrgItemList.size()>0) {
            tCircleTeamsUuids.forEach( uuid -> {
                sharingGroupOrgItemList.forEach(sgoi -> {
                    // If the uuid is found, just update the hashmap in order to know what team is already there
                    if (uuid.equals(sgoi.getOrganisation().getUuid()))
                        sGroupOrgCheckMap.put(uuid,true);
                });
            });
        } else {
            sGroup.setSharingGroupOrg(new ArrayList<SharingGroupOrgItem>());
            tCircleTeamsUuids.forEach(uuid ->sGroupOrgCheckMap.put(uuid, false));
        }

        // For any false value in the map, get the corresponding keys' organisations in MISP and add it in the sharing
        // group's list of organisations (list of SharingGroupOrgItems).
        sGroupOrgCheckMap.forEach((k,v)-> {
            if (v == false) {
                // Sharing Group method for adding a sharingGroupOrgItem which contains an organisation with this uuid.
                SharingGroupOrgItem newSgOrgItem = new SharingGroupOrgItem();
                // Since organisations are synchronized first, the organisation in MISP with this UUID key should exist;
                // we just need to fetch it and assign it to the current sharing group.
                OrganisationDTO newOrg = mispAppClient.getMispOrganisation(k);
                newSgOrgItem.setOrganisation(newOrg);
                sGroup.addSharingGroupOrgItem(newSgOrgItem);
            }
        });

    }

}
