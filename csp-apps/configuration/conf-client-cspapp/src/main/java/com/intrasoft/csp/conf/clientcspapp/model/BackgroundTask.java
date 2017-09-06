package com.intrasoft.csp.conf.clientcspapp.model;

/**
 * Created by tangelatos on 06/09/2017.
 */
public interface BackgroundTask<S, R> {

    BackgroundTaskResult<S, R> execute();
}
