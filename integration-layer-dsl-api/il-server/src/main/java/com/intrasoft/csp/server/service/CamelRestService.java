package com.intrasoft.csp.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.exceptions.CspBusinessException;
import org.apache.camel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public <T> List<T> sendAndGetList(String uri, Object obj , String httpMethod, Class<T> tClass, Map<String,Object> headers) throws IOException {
        String out = sendBodyAndHeaders(uri,obj, httpMethod,headers);
        return objectMapper.readValue(out, objectMapper.getTypeFactory().constructCollectionType(List.class,tClass));
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass) throws IOException {
        String out = send(uri,obj, httpMethod);
        return objectMapper.readValue(out, tClass);
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass,Map<String,Object> headers) throws IOException {
        String out = sendBodyAndHeaders(uri,obj, httpMethod,headers);
        return objectMapper.readValue(out, tClass);
    }

    public String send(String uri, Object obj, String httpMethod) throws IOException {
        return sendBodyAndHeaders(uri,obj,httpMethod,null);
    }

    public String sendBodyAndHeaders(String uri, Object obj, String httpMethod, Map<String,Object> headers) throws JsonProcessingException {
        byte[] b = objectMapper.writeValueAsBytes(obj);
        Exchange exchange = producerTemplate.send(uri, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                if(headers!=null){
                    for (Map.Entry<String, Object> entry : headers.entrySet()) {
                        exchange.getIn().setHeader(entry.getKey(), entry.getValue());
                    }

                }
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
