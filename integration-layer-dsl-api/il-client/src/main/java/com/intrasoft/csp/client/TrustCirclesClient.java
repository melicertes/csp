package com.intrasoft.csp.client;

import com.intrasoft.csp.commons.model.TrustCircle;

/**
 * Created by iskitsas on 4/8/17.
 */
public interface TrustCirclesClient {
    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    TrustCircle getTrustCircle(Integer id);
}
