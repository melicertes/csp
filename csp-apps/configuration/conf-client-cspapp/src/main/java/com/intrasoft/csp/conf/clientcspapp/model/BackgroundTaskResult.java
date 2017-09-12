package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Getter @Setter @AllArgsConstructor @ToString @EqualsAndHashCode
public class BackgroundTaskResult<S, R> {

    private S source;
    private R result;


}
