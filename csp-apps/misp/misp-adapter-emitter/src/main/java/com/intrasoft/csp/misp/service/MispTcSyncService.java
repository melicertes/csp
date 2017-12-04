package com.intrasoft.csp.misp.service;

//  Synchronizes existing TrustCircles Teams and existing Trust Cicles with MISP's Organisations and Sharing Groups

public interface MispTcSyncService {

//    TODO: What do we really want this method to return? Just a boolean indication of success or more information?
//    Maybe a custom class to be created for this method to return?
//    What happens in case there is a problem when updating a certain team or sharing group for example?

      void sync();


}
