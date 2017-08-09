package com.intrasoft.csp.conf.commons.interfaces;


import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


public interface ConfigurationApi {

    public ResponseEntity updates(String cspId);

    public ResponseEntity register(String cspId, RegistrationDTO cspRegistration);

    public ResponseEntity update(String cspId, String updateHash);

    public ResponseEntity appInfo(String cspId, AppInfoDTO appInfo);
}
