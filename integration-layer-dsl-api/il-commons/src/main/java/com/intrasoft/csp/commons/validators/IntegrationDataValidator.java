package com.intrasoft.csp.commons.validators;

import com.intrasoft.csp.commons.model.IntegrationData;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by iskitsas on 6/9/17.
 */
@Component
public class IntegrationDataValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return IntegrationData.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors,"dataParams","dataParams.empty");
        ValidationUtils.rejectIfEmpty(errors,"sharingParams","sharingParams.empty");
        ValidationUtils.rejectIfEmpty(errors,"dataType","dataType.empty");
    }
}
