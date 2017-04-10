package com.sastix.csp.commons.model;

import java.io.Serializable;

/**
 * Created by iskitsas on 4/11/17.
 */
public class TrustCircleEcspDTO implements Serializable {
    private static final long serialVersionUID = 9056613423375285570L;
    TrustCircle trustCircle;
    IntegrationData integrationData;

    public TrustCircleEcspDTO() {
    }

    public TrustCircleEcspDTO(TrustCircle trustCircle, IntegrationData integrationData) {
        this.trustCircle = trustCircle;
        this.integrationData = integrationData;
    }

    public TrustCircle getTrustCircle() {
        return trustCircle;
    }

    public void setTrustCircle(TrustCircle trustCircle) {
        this.trustCircle = trustCircle;
    }

    public IntegrationData getIntegrationData() {
        return integrationData;
    }

    public void setIntegrationData(IntegrationData integrationData) {
        this.integrationData = integrationData;
    }
}
