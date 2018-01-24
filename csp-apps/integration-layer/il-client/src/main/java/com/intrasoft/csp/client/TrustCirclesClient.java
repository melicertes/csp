package com.intrasoft.csp.client;

import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;

import java.util.List;

/**
 * Created by iskitsas on 4/8/17.
 *
 */
public interface TrustCirclesClient {
    void setProtocolHostPort(String protocol, String host, String port);

    String getContext();

    List<TrustCircle> getAllTrustCircles();

    List<Team> getAllTeams();

    TrustCircle getTrustCircleByUuid(String uuid);

    Team getTeamByUuid(String uuid);

    List<TrustCircle> getAllLocalTrustCircles();

    TrustCircle getLocalTrustCircleByUuid(String uuid);

}
