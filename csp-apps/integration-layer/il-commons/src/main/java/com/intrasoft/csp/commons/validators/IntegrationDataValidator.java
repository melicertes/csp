package com.intrasoft.csp.commons.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.SharingParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.io.IOException;
import java.util.List;

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
            // This isValidJson is redundant since the validity has already been checked by spring jackson mapper when received the
            // message at the controller. In case of an invalid json the platorm will have already thrown an exception,
            // handled by CspExceptionHandlingController.
            // Will leave this section of code to demonstrate how someone can enhance the existing SpringValidatorAdapter
            // with custom rules.
            boolean isValidJson = isValidJSON(((IntegrationData)obj).getDataObject());
            if(!isValidJson){
                errors.reject("IntegrationData.dataObject is not a valid json. ","integrationdata.dataobject.not.valid");
            }
            String errorTcTeamFieldMessage = validTcTeamField((IntegrationData)obj);
            if(!StringUtils.isEmpty(errorTcTeamFieldMessage)){
                errors.reject("IntegrationData.sharingParams inlcude not valid properties. "+errorTcTeamFieldMessage,"integrationdata.sharingparams.not.valid");
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
        return valid;
    }

    public String validTcTeamField(IntegrationData integrationData) {
        String ret = null;
        SharingParams sharingParams = integrationData.getSharingParams();
        if (sharingParams != null) {
            //TRUST_CIRCLE
            if (sharingParams.getTcId() != null) {
                //should be a List or String
                if (sharingParams.getTcId() instanceof List) {
                    for (Object obj : (List) sharingParams.getTcId()) {
                        if (obj instanceof String) {
                            //good to go
                            if(StringUtils.isEmpty(obj)){
                                ret = "trustCircle array should include non empty string items";
                                break;
                            }
                        } else {
                            ret = " trustCircleId should be an array of strings ";
                            break;
                        }
                    }
                } else if (sharingParams.getTcId() instanceof String) {
                    //good to go
                    if(StringUtils.isEmpty(sharingParams.getTcId())){
                        ret = "trustCircleId should NOT be an empty string, use null if you do not want to be used.";
                    }
                } else {
                    ret = " trustCircleId should be an array of strings or a single string value.";
                }
            } else if (sharingParams.getTeamId() != null) {
                // TEAM
                //should be a List or String
                if (sharingParams.getTeamId() instanceof List) {
                    for (Object obj : (List) sharingParams.getTeamId()) {
                        if (obj instanceof String) {
                            //good to go
                            if(StringUtils.isEmpty(obj)){
                                ret = "teamId array should include non empty string items";
                                break;
                            }
                        } else {
                            ret = " teamId should be an array of strings ";
                            break;
                        }
                    }
                } else if (sharingParams.getTeamId() instanceof String) {
                    //good to go
                    if(StringUtils.isEmpty(sharingParams.getTcId())){
                        ret = "teamId should NOT be an empty string, use null if you do not want to be used.";
                    }
                } else {
                    ret = " teamId should be an array of strings or a single string value.";
                }
            }
        }
        return ret;
    }
}
