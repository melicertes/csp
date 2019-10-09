package com.intrasoft.csp.misp.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.misp.commons.config.MispContextUrl;
import org.json.JSONObject;

import java.io.IOException;

public interface EmitterDataHandler {

    public void handleMispData(Object object, MispContextUrl.MispEntity mispEntity, boolean isReEmittion, boolean isDelete);
}
