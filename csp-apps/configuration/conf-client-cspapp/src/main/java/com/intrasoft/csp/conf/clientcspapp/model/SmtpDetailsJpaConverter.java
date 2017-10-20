package com.intrasoft.csp.conf.clientcspapp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

/**
 * Created by tangelatos on 10/09/2017.
 */
@Converter
public class SmtpDetailsJpaConverter implements AttributeConverter<SmtpDetails, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(SmtpDetails meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public SmtpDetails convertToEntityAttribute(String dbData) {
        try {
            return dbData == null? null : objectMapper.readValue(dbData, SmtpDetails.class);
        } catch (IOException ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }
}
