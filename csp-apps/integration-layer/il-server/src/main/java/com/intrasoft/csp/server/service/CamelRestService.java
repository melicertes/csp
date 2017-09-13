package com.intrasoft.csp.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import org.apache.camel.*;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
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

    public String send(String uri, Object obj, String httpMethod,boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery) throws IOException {
        return sendBodyAndHeaders(uri,obj,httpMethod,null,checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery);
    }

    public String sendBodyAndHeaders(String uri, Object obj, String httpMethod, Map<String,Object> headers) throws JsonProcessingException {
        return sendBodyAndHeaders(uri, obj, httpMethod, headers, false);
    }


    public String sendBodyAndHeaders(String uri, Object obj, String httpMethod, Map<String,Object> headers,
                                     boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery) throws JsonProcessingException {
        //objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        //objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
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
            //TODO: Redelivery only at specific 5xx and maybe 408. More researched is needed
            //HttpHostConnectException might be raised from TC calls
            if(checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery
                    &&  exception.getClass().equals(HttpOperationFailedException.class)
                    && ((HttpOperationFailedException) exception).getStatusCode()>= 400
                    && ((HttpOperationFailedException) exception).getStatusCode()< 500){
                HttpOperationFailedException ef = (HttpOperationFailedException) exception;
                LOG.error("ENDPOINT "+uri+" responded with statusCode "+ef.getStatusCode() + " "+ef.getStatusText());
            }else {
                throw new CspBusinessException("Exception in external request: " + exception.getMessage(), exception);
            }
        }
        return out;
    }
}
