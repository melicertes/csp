package com.intrasoft.csp.misp.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface EmitterAuditLogHandler {
    public void handleAuditLog(JsonNode object) throws IOException;
}
