package com.intrasoft.csp.misp.service;

//  Synchronizes existing TrustCircles Teams and existing Trust Cicles with MISP's Organisations and Sharing Groups

import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;

public interface MispTcSyncService {

      void syncAll();

      void syncOrganisations();

      void syncSharingGroups();

      OrganisationDTO mapTeamToOrganisation(Team team, OrganisationDTO organisation);

      SharingGroup mapTrustCircleToSharingGroup(TrustCircle tCircle, SharingGroup sGroup);
}
