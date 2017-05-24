package com.sastix.csp.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.csp.commons.exceptions.CspBusinessException;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iskitsas on 4/8/17.
 */
@Service
public class CamelRestService {
    private static final Logger LOG = LoggerFactory.getLogger(CamelRestService.class);
    @Autowired
    ObjectMapper objectMapper;

    @Produce
    private ProducerTemplate producerTemplate;

    @Value("${CSRFToken}")
    String csrfToken;

    @Value("${Authorization}")
    String authorization;

    public <T> ArrayList<T> sendTc(String uri, Object obj , String httpMethod, Class<T> tClass) throws IOException {
        String out = send(uri,obj, httpMethod);
        return objectMapper.readValue(out, new TypeReference<List<T>>() { });
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass) throws IOException {
        String out = send(uri,obj, httpMethod);
        return objectMapper.readValue(out, tClass);
    }

    public String send(String uri, Object obj, String httpMethod) throws IOException {
        byte[] b = objectMapper.writeValueAsBytes(obj);
        Exchange exchange = producerTemplate.send(uri, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                exchange.getIn().setHeader("X-CSRFToken", csrfToken);
                exchange.getIn().setHeader("Authorization",authorization);
                exchange.getIn().setBody(b);
            }
        });

        String out = exchange.getOut().getBody(String.class);
        Exception exception = exchange.getException();
        Boolean isExternalRedelivered = exchange.isExternalRedelivered();
        Boolean isFailed = exchange.isFailed();
        if(isFailed && exception != null){
            throw new CspBusinessException("Exception in external request: "+exception.getMessage(),exception);
        }
        return out;
    }
}
