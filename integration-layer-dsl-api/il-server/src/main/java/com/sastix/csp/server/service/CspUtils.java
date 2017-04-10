package com.sastix.csp.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Created by iskitsas on 4/11/17.
 */
@Service
public class CspUtils {
    @Autowired
    Environment env;

    public String getAppUri(String appName){
        String appProtocol = env.getProperty(appName+".protocol");
        String appHost = env.getProperty(appName+".host");
        String appPort = env.getProperty(appName+".port");
        String appPath = env.getProperty(appName+".path");

        String appUri = appProtocol+"://"+appHost+":"+appPort+appPath;
        return appUri;
    }
}
