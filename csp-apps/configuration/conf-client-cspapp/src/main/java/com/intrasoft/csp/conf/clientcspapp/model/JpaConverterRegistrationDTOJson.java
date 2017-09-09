package com.intrasoft.csp.conf.clientcspapp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Converter
public class JpaConverterRegistrationDTOJson implements AttributeConverter<RegistrationDTO, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(RegistrationDTO meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public RegistrationDTO convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, RegistrationDTO.class);
        } catch (IOException ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

}
