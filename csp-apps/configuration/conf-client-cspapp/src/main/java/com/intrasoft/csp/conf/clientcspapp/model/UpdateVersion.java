package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

/**
 * Created by tangelatos on 09/09/2017.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ToString @EqualsAndHashCode
@Builder
public class UpdateVersion {

    private String name;
    private String description;
    private String version;
    private String versionInstalled;
    private String released;
    private String hash;
    private Integer priority;
    private String btn;

}
