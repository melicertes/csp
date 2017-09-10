package com.intrasoft.csp.conf.clientcspapp.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intrasoft.csp.conf.commons.model.forms.CspForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by tangelatos on 10/09/2017.
 */
@Getter
@Setter @NoArgsConstructor
@ToString
public class RegistrationForm extends CspForm {
    @JsonProperty("smtp_host")
    private String smtp_host;

    @JsonProperty("smtp_port")
    private String smtp_port;

    @JsonProperty("smtp_user")
    private String smtp_user;

    @JsonProperty("smtp_pass")
    private String smtp_pass;


}
