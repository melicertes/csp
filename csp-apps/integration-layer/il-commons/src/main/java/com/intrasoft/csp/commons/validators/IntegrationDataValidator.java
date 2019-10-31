package com.intrasoft.csp.commons.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.SharingParams;
import net.openhft.hashing.LongHashFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
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
            // This isValidJson is redundant since the validity has already been checked by spring jackson mapper when received the
            // message at the controller. In case of an invalid json the platorm will have already thrown an exception,
            // handled by CspExceptionHandlingController.
            // Will leave this section of code to demonstrate how someone can enhance the existing SpringValidatorAdapter
            // with custom rules.
            boolean isValidJson = isValidJSON(((IntegrationData)obj).getDataObject());
            if(!isValidJson){
                errors.reject("IntegrationData.dataObject is not a valid json. ","integrationdata.dataobject.not.valid");
            }
            String hmac = ((IntegrationData)obj).getHmac();
            //now set it to "xx" and try to generate the hmac again
            signHMAC((IntegrationData)obj);
            String computed = ((IntegrationData) obj).getHmac();
            if (computed != null) {
                if (!computed.contentEquals(hmac)) {
                    LOG.error("signature does not match - computed {}, in object {}", computed, hmac);
                    errors.reject("IntegrationData.hmac not valid.");
                }
            } else {
                LOG.error("signature cannot be computed - object is null -> {}", obj);
            }


            String errorTcTeamFieldMessage = validTcTeamField((IntegrationData)obj);
            if(!StringUtils.isEmpty(errorTcTeamFieldMessage)){
                errors.reject("IntegrationData.sharingParams inlcude not valid properties. "+errorTcTeamFieldMessage,"integrationdata.sharingparams.not.valid");
            }
        } catch (IOException e) {
            LOG.error("IOException while validating IntegrationData.dataObject",e);
        }
    }

    private void signHMAC(IntegrationData integrationData) {
        integrationData.setHmac("xx");
        integrationData.setHmac(Long.toHexString(LongHashFunction.xx(54018521).hashChars(integrationData.toString())));
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
            if (sharingParams.getTrustCircleIds() != null) {
                //should be a List or String
                if (sharingParams.getTrustCircleIds() != null) {
                    for (String tcid : sharingParams.getTrustCircleIds()) {
                        if(StringUtils.isEmpty(tcid)){
                            ret = "trustCircle array should include non empty string items";
                            return ret;
                        }
                    }
                } else {
                    ret = " trustCircleId should be an array of strings.";
                }
            } else if (sharingParams.getTeamIds() != null) {
                // TEAM
                //should be a List or String
                if (sharingParams.getTeamIds() != null) {
                    for (String tid : sharingParams.getTeamIds()) {
                        if (StringUtils.isEmpty(tid)) {
                            ret = "teamId array should include non empty string items";
                            return ret;
                        }
                    }
                } else {
                    ret = " teamId should be an array of strings ";
                }
            }
        }
        return ret;
    }
}
