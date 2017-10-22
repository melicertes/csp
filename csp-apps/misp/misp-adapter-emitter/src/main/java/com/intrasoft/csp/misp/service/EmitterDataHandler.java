package com.intrasoft.csp.misp.service;


import java.io.IOException;

public interface EmitterDataHandler {

    public void handleMispData(String content) throws IOException;
}
