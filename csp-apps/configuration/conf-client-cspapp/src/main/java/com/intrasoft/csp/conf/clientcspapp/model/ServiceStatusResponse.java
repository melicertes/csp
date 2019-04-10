package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ServiceStatusResponse {

    int runningServices;

    ServiceState serverStatus;

    @Builder.Default
    String msg = "Processed OK";

}
