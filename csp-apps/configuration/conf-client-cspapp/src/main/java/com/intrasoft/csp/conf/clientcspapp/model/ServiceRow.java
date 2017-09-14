package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

/**
 * Created by tangelatos on 13/09/2017.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString @Builder
public class ServiceRow {
    String name;
    String currentState;
    String startable;
    Integer startPriority;

    String version;

}
