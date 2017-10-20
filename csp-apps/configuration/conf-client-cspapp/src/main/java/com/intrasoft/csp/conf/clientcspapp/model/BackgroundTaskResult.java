package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Getter @Setter @RequiredArgsConstructor @AllArgsConstructor @ToString @EqualsAndHashCode
public class BackgroundTaskResult<S, R> {

    @NonNull
    private S success;
    @NonNull
    private R errorCode;


    private String moduleName;
}
