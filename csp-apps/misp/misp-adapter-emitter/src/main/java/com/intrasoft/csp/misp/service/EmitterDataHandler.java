package com.intrasoft.csp.misp.service;


import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;

import java.io.IOException;

public interface EmitterDataHandler {

    public void handleMispData(Object object) throws IOException;
}
