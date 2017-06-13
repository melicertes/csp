package com.intrasoft.csp.commons.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.io.IOException;

/**
 * Created by iskitsas on 6/9/17.
 */

public class IntegrationDataValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(IntegrationDataValidator.class);
    private final SpringValidatorAdapter validator;

    public IntegrationDataValidator(SpringValidatorAdapter validator) {
        this.validator = validator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return IntegrationData.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        //jsr303
        validator.validate(obj, errors);

        //custom rules
        try {
            boolean isValidJson = isValidJSON(((IntegrationData)obj).getDataObject());
            if(!isValidJson){
                errors.reject("IntegrationData.dataObject is not a valid json. ","integrationdata.dataobject.not.valid");
            }
        } catch (IOException e) {
            LOG.error("IOException while validating IntegrationData.dataObject",e);
        }
    }

    public boolean isValidJSON(final Object jsonObj) throws IOException {
        String json = null;
        boolean valid = true;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(jsonObj);
            objectMapper.readTree(json);
        } catch(JsonProcessingException e){
            valid = false;
        }
        //return json !=null && valid && isValidJSON(json);
        return valid;
    }

    public boolean isValidJSON(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
