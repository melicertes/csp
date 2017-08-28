package com.intrasoft.csp.server.policy.domain;

public interface SharingPolicyRoutes {
    /**
     * URLs
     * */
    String BASE_URL = "/policies";
    String SAVE_URL = "/"+BASE_URL+"/save";
    String DELETE_URL = "/"+BASE_URL+"/delete";

    /**
     * Thymeleaf View templates
     * */

    String HOME_TH = "home";
}
