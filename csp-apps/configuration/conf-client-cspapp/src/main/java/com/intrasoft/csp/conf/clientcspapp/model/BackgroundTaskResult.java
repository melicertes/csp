package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Getter @Setter @AllArgsConstructor @ToString
public class BackgroundTaskResult<S, R> {

    private S source;
    private R result;


}
