package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

@Getter
@Setter
public class BackgroundTaskImpl<S, R> implements BackgroundTask<S, R> {

    private final String description;
    private final BackgroundTask<S,R> delegate;
    private LocalDateTime completionDate;

    public BackgroundTaskImpl(String description, BackgroundTask<S,R> delegate) {
        this.description = description;
        this.delegate = delegate;
    }

    @Override
    public BackgroundTaskResult<S, R> execute() {
        return delegate.execute();
    }
}
