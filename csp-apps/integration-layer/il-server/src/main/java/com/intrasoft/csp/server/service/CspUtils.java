package com.intrasoft.csp.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.routes.ContextUrl;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by iskitsas on 4/11/17.
 */
@Service
public class CspUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CspUtils.class);
    @Autowired
    Environment env;

    @Autowired
    ObjectMapper objectMapper;

    public String getAppUri(String appName){
        LOG.debug(appName);
        String appProtocol = env.getProperty(ContextUrl.APP_PROPERTIES_PREFIX+"."+appName+".protocol");
        String appHost = env.getProperty(ContextUrl.APP_PROPERTIES_PREFIX+"."+appName+".host");
        String appPort = env.getProperty(ContextUrl.APP_PROPERTIES_PREFIX+"."+appName+".port");
        String appPath = env.getProperty(ContextUrl.APP_PROPERTIES_PREFIX+"."+appName+".path");

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
