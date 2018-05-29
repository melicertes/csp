package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.client.TrustCirclesClient;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.misp.client.MispAppClient;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroupOrgItem;
import com.intrasoft.csp.misp.service.Conversions;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

@Service
public class MispTcSyncServiceImpl implements MispTcSyncService,Conversions {

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

    @Value("${misp.sync.prefix}")
    String prefix;

    // Trust Circles (either LTCs or CTCs) having these strings as their short name will not synchronize in MISP.
    String[] trustCircleNamesExcluded = { "CTC::CSP_ALL", "CTC::CSP_SHARING", "LTC::CSP_SHARING" };

//  TODO: Investigate which additional fields can be mapped

    @PostConstruct
    private void init() {
        LOG.info(" -- Misp sync "+(syncEnabled?"enabled":"disabled"));
    }

    @Scheduled(fixedDelayString = "${misp.sync.fixed.delay}", initialDelayString = "${misp.sync.initial.delay}")
    @Override
    public void syncAll() {
        if(syncEnabled) {
            LOG.debug("Misp sync triggered. Will sync all.");
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

        // check misp orgs to decide if we do a 'create' or 'update'
        if(teamList!=null && teamList.size()>0) {
            for (int i=0; i<teamList.size(); i++) {
                OrganisationDTO organisation = getOrgIfExistsInTeams(teamList.get(i), orgList);
                if (organisation == null) {
                    //CREATE Misp Organization
                    //------------------------
                    OrganisationDTO organisationDTO = mapTeamToOrganisation(teamList.get(i),null);
                    try {
                        mispAppClient.addMispOrganisation(organisationDTO);
                    }catch (Exception e){
                        LOG.error(String.format("Error while adding misp org: %s. Logging the error and continue",organisationDTO.getName()),e);
                    }
                }else{
                    //UPDATE Misp Organization
                    //------------------------
                    // Uuid Match
                    if (teamList.get(i).getId().equals(organisation.getUuid())) {
                        // Updating the MISP organisation with the TC team data.
                        mispAppClient.updateMispOrganisation(mapTeamToOrganisation(teamList.get(i), organisation));
                    }
                }
            }
        }else{
            LOG.debug("No teams found during sync..");
        }


        // Making the necessary calls to MISP, depending on TC's Teams content.
//        boolean loopBreak;
//        for (int i=0; i<teamList.size(); i++) {
//            loopBreak = false;
//            for (int j=0; j<orgList.size(); j++) {
//                OrganisationDTO organisation = orgList.get(j);
//                // Uuid Match
//                if (teamList.get(i).getId().equals(organisation.getUuid())) {
//                    // Updating the MISP organisation with the TC team data.
//                    mispAppClient.updateMispOrganisation(mapTeamToOrganisation(teamList.get(i), organisation));
//                    loopBreak = true;
//                    break;
//                }
//            }
//            if (loopBreak) continue;
//            // No match; create this team as an organisation in MISP.
//
//            OrganisationDTO organisationDTO = mapTeamToOrganisation(teamList.get(i),null);
//            try {
//                mispAppClient.addMispOrganisation(organisationDTO);
//            }catch (Exception e){
//                LOG.error(String.format("Error while adding misp org: %s. Logging the error and continue",organisationDTO.getName()),e);
//            }
//        }

        // Finding orphan MISP organisations
        findOrphanOrgs(teamList);
    }

    private OrganisationDTO getOrgIfExistsInTeams(Team team, List<OrganisationDTO> orgList){
        OrganisationDTO ret = null;
        if(orgList!=null){
            ret = orgList.stream().filter(org->org.getUuid().equals(team.getId())).findFirst().orElse(null);
        }
        return ret;
    }


    public void findOrphanOrgs(List<Team> teamList){
        // Refreshing our lists first
        List<OrganisationDTO> orgList = mispAppClient.getAllMispOrganisations();
//        teamList = trustCirclesClient.getAllTeams();
        List<String> teamIdList = new ArrayList<>();
        boolean loopBreak;
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

        LOG.trace("Found " + teamIdList.size() + " orphan organisations in MISP");
        // TODO: What is the action to be taken when a MISP organisation does not have a corresponding team in TC? (deletion is not an option for now)
        // teamIdList.forEach(id -> mispAppClient.deleteMispOrganisation(id));
    }

//  TODO: Sharing Groups API response on GET calls for all Sharing Groups does not include their UUIDs.
//  This could be an overkill considering all the extra GET calls.
    public void syncSharingGroups() {

        List<TrustCircle> tcList = trustCirclesClient.getAllTrustCircles();

        // Adding Local Trust Circles to existing Central Trust Circles list for synchronization;
        List<TrustCircle> localTcList = trustCirclesClient.getAllLocalTrustCircles();
        List<TrustCircle> combinedList = new ArrayList<>();
        combinedList.addAll(tcList);
        combinedList.addAll(localTcList);
        tcList = combinedList;

        tcList = excludeTrustCirclesFromSyncByShortName(tcList);

        List<SharingGroup> sgList = getAllSharingGroupsWithUuids();

        boolean loopBreak;
        for (int i=0; i<tcList.size(); i++) {
            loopBreak=false;
            for (int j=0; j<sgList.size(); j++) {
                SharingGroup sharingGroup = sgList.get(j);
                // Uuid Match
                if (tcList.get(i).getId().equals(sharingGroup.getUuid())) {
                    // Populating this MISP sharing group with the matching TC trust circle data and updating MISP.
                    mispAppClient.updateMispSharingGroup(mapTrustCircleToSharingGroup(tcList.get(i), sharingGroup));
                    loopBreak=true;
                    break;
                }
            }
            if (loopBreak) continue;
            // No match; create this Trust Circle as a Sharing Group in MISP.
            mispAppClient.addMispSharingGroup(mapTrustCircleToSharingGroup(tcList.get(i), null));
        }

        // SXCSP-435 Setting Sharing Groups as inactive instead of deleting them
        // SXCSP-455 Adding date suffix to the passive Sharing Groups

        sgList = getAllSharingGroupsWithUuids(); // refresh list

        boolean exists;
        for (SharingGroup sg : sgList) {
            exists = tcList.stream().anyMatch(tc -> tc.getId().equals(sg.getUuid()));
            if (!exists) { // if Sharing Group's UUID doesn't exist anywhere in Trust Circles
                if (sg.isActive())  // prevents suffix from being added multiple times
                    sg.setName(addDateSuffix(sg.getName())); // SXCSP-455
                sg.setActive(false);
                mispAppClient.updateMispSharingGroup(sg);
            }
        }

    }

    public OrganisationDTO mapTeamToOrganisation(Team team, OrganisationDTO organisation) {
        OrganisationDTO newOrganisation = null;
        if(organisation == null){
            //Organization will be created from scratch
            newOrganisation = new OrganisationDTO();
        }else{
            //map to new object
            newOrganisation = copyOrganization.apply(organisation);
        }

        newOrganisation.setUuid(team.getId());
        // SXCSP-436 Mapping Teams NIS Sectors to Organisations Sector
        List<String> teamSectors = team.getNisSectors();
        String orgSectors = new String();
        if (teamSectors.size()>0) {
            for (String sector : teamSectors) {
                orgSectors+=sector+", ";
            }
            orgSectors = orgSectors.substring(0, orgSectors.lastIndexOf(", "));
        }
        newOrganisation.setSector(orgSectors);
        // Modifying the name field to differentiate synchronized organisations.
        newOrganisation.setName(prefix + team.getShortName());
        newOrganisation.setDescription(team.getDescription());
        newOrganisation.setNationality(team.getCountry());
        // SXCSP-420: "The team with the csp-id that is equal to this csp-id should be imported as local org."
        if (team.getCspId().equals(cspId))  // case-sensitivity?
            newOrganisation.setLocal(true);
        else
            newOrganisation.setLocal(false);

        return newOrganisation;
    }

    public SharingGroup mapTrustCircleToSharingGroup(TrustCircle tCircle, SharingGroup sGroup) {
        final boolean isNew = (sGroup == null) ? true : false;
        if(sGroup == null){
            //SharingGroup will be created from scratch
            sGroup = new SharingGroup();
        }
        sGroup.setUuid(tCircle.getId());
        // Modifying the name field to differentiate synchronized sharing groups.
        sGroup.setName(prefix + tCircle.getShortName());
        sGroup.setDescription(tCircle.getDescription());
        sGroup.setReleasability(""); // informational but mandatory;
        sGroup.setActive(true);

        List<String> tCircleTeamsUuids = tCircle.getTeams();
        List<SharingGroupOrgItem> sharingGroupOrg = (sGroup.getSharingGroupOrg() == null) ?
                new ArrayList<>() : sGroup.getSharingGroupOrg();

        // Mapping existing/non-existing Organisations UUIDs to true/false
        Map<String, Boolean> sGroupOrgCheckMap = new HashMap<>();
        tCircleTeamsUuids.forEach( uuid -> {
            sGroupOrgCheckMap.put(uuid, (mispAppClient.getMispOrganisation(uuid)!=null));
        });

        List<String> sGroupOrgsUuids = new ArrayList<>();
        sGroup.setSharingGroupOrg(sharingGroupOrg);  // TODO: May introduce bug; check
        sGroup.getAllOrganisations().forEach(orgDTO -> {
            sGroupOrgsUuids.add(orgDTO.getUuid());
        });
        String sGroupUuid = sGroup.getUuid();

        // Synchronize the corresponding Sharing Group's Organisation content with the given Trust Circle's Team content.
        // The Organisations themselves should have already been synchronized in MISP.
        sGroupOrgCheckMap.forEach((k,v)-> {
            if (v) { // If the Organisation already exists in MISP
                OrganisationDTO organisationDTO = mispAppClient.getMispOrganisation(k);
                // SXCSP-454 (Add Organisation to Sharing Group)
                // If not already referenced in the Sharing Group,
                if (!sGroupOrgsUuids.stream().filter(uuid -> uuid.equals(k)).findFirst().isPresent()) {
                    if (!isNew) { // This API operation requires the Sharing Group to already exist in MISP
                        mispAppClient.updateMispSharingGroupAddOrganisation(sGroupUuid, k);
                    }
                    sharingGroupOrg.add(addOrgAsSGOI(organisationDTO));
                }
            } else if (!v) {
                LOG.debug("Organisation with UUID " + k + " has not been synchronized yet");
            }
        });
        if (!(tCircleTeamsUuids.size()>0)) {
            sGroupOrgsUuids.forEach(orgUuid -> {
                if (mispAppClient.updateMispSharingGroupRemoveOrganisation(sGroupUuid, orgUuid)) {
                    // log info after successful operation?
                } else {
                    LOG.debug("Organisation(%s) could not be removed from Sharing Group(%s)", orgUuid, sGroupUuid);
                }
            });
            sGroup.setSharingGroupOrg(null);
        }
        else if (sharingGroupOrg.size() > 0) {
            // Remove any Organisations from the Sharing Group that don't match the given Trust Circle's Team UUID content.
            List<String> orgsToRemoveFromSG = removeUnlistedOrgs(tCircleTeamsUuids, sGroupOrgsUuids);
            orgsToRemoveFromSG.forEach(orgUuid -> {
                if (mispAppClient.updateMispSharingGroupRemoveOrganisation(sGroupUuid, orgUuid)) {
                    // log info after successful operation?
                } else {
                    LOG.debug("Organisation(%s) could not be removed from Sharing Group(%s)", orgUuid, sGroupUuid);
                }
            });
            // Can't use UUID when getting a Sharing Group; use id instead. Otherwise, MISP returns "500 internal error".
            if (!isNew) {
                SharingGroup updatedSharingGroup = mispAppClient.getMispSharingGroup(sGroup.getId());
                List<OrganisationDTO> updatedOrgList = updatedSharingGroup.getAllOrganisations();
                sharingGroupOrg.clear();
                updatedOrgList.forEach(orgDTO -> {
                    sharingGroupOrg.add(addOrgAsSGOI(orgDTO));
                });
            }

            sGroup.setSharingGroupOrg(sharingGroupOrg);
        }
        // Prevents MISP error 403 when including an empty organisations array; MISP accepts null instead
        if (isNew && sGroup.getSharingGroupOrg() != null) {
            if (sGroup.getSharingGroupOrg().isEmpty())
                sGroup.setSharingGroupOrg(null);
        }
        return sGroup;
    }

    public List<TrustCircle> excludeTrustCirclesFromSyncByShortName(List<TrustCircle> tcList) {
        Predicate<TrustCircle> tcPredicate;
        for (String shortName : trustCircleNamesExcluded) {
            tcPredicate = tc -> tc.getShortName().equals(shortName);
            tcList.removeIf(tcPredicate);
        }
        return tcList;
    }

    private SharingGroupOrgItem addOrgAsSGOI(OrganisationDTO organisationDTO) {
        SharingGroupOrgItem sgoi = new SharingGroupOrgItem();
        sgoi.setOrganisation(organisationDTO);
        sgoi.setExtend(false);
        sgoi.setOrgId(organisationDTO.getId());
        return sgoi;
    }

    // Using this private method until API GET call for All Sharing Groups includes their UUIDs.
    private List<SharingGroup> getAllSharingGroupsWithUuids() {
        List<SharingGroup> sgList = mispAppClient.getAllMispSharingGroups();

//      Temporary fix for unknown Sharing Group UUIDs; extra calls on server
        sgList.forEach(sharingGroup ->  {
            sharingGroup.setUuid(mispAppClient.getMispSharingGroup(sharingGroup.getId()).getUuid());
        });
        return sgList;
    }

    // Filters out any Organisation UUIDs from the Sharing Group that don't exist in the corresponding Trust Circle.
    private List<String> removeUnlistedOrgs(List<String> teamUuids, List<String> orgUuids) {
        List<String> orgUuidsToRemove = new ArrayList<>();
        orgUuids.forEach( orgUuid -> {
            if (!teamUuids.stream().anyMatch(teamUuid -> teamUuid.equals(orgUuid))) {
                orgUuidsToRemove.add(orgUuid);
            }
        });
        return orgUuidsToRemove;
    }

    // SXCSP-455 Adding date suffix to the passive Sharing Groups
    private String addDateSuffix(String name) {
        Date date = Calendar.getInstance().getTime();
        String mispDatePattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(mispDatePattern);
        return name + "_" + sdf.format(date);
    }

}
