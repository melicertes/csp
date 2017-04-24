package com.sastix.csp.client;

import com.sastix.csp.commons.model.TrustCircle;

import java.util.List;

/**
 * Created by iskitsas on 4/8/17.
 */
public interface TrustCirclesClient {
    void setProtocolHostPort(String protocol, String host, String port);
    String getContext();
   TrustCircle getTrustCircle(Integer id);
}
