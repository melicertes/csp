package com.intrasoft.csp.misp.service;

import java.io.IOException;

public interface EmitterAuditLogHandler {
    public void handleAuditLog(Object object) throws IOException;
}
