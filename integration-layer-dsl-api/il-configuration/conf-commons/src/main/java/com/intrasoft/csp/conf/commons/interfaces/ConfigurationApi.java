package com.intrasoft.csp.conf.commons.interfaces;


import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


public interface ConfigurationApi {

    ResponseEntity updates(String cspId);

    ResponseEntity register(String cspId, RegistrationDTO cspRegistration);

    ResponseEntity update(String cspId, String updateHash);

    ResponseEntity appInfo(String cspId, AppInfoDTO appInfo);
}
