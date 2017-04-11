package com.sastix.csp.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by iskitsas on 4/11/17.
 */
@Service
public class CspUtils {
    @Autowired
    Environment env;

    @Autowired
    ObjectMapper objectMapper;

    public String getAppUri(String appName){
        String appProtocol = env.getProperty(appName+".protocol");
        String appHost = env.getProperty(appName+".host");
        String appPort = env.getProperty(appName+".port");
        String appPath = env.getProperty(appName+".path");

        String appUri = appProtocol+"://"+appHost+":"+appPort+appPath;
        return appUri;
    }

    public <T> T getExchangeData(Exchange exchange,Class<T> tClass) throws IOException {
        T data = exchange.getIn().getBody(tClass);
        if(data == null){//try bytes in case of null
            String inData = exchange.getIn().getBody(String.class);
            data = objectMapper.readValue(inData, tClass);
        }
        return data;
    }
}
