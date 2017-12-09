package com.intrasoft.csp.misp.service;

//  Synchronizes existing TrustCircles Teams and existing Trust Cicles with MISP's Organisations and Sharing Groups

public interface MispTcSyncService {

      void syncAll();

      public void syncOrganisations();

      public void syncSharingGroups();


}
