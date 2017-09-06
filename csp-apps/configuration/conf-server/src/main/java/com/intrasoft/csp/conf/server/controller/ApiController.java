package com.intrasoft.csp.conf.server.controller;

import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.AppInfoDTO;
import com.intrasoft.csp.conf.commons.model.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.UpdateInformationDTO;
import com.intrasoft.csp.conf.server.service.ConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiContextUrl.API_BASEURL)
public class ApiController implements ApiContextUrl {

    private static Logger LOG_AUDIT = LoggerFactory.getLogger("audit-log");
    private static Logger LOG_EXCEPTION = LoggerFactory.getLogger("exc-log");

    @Autowired
    ConfService confService;

    @RequestMapping(value = "/v" + REST_API_V1 + API_UPDATES + "/{cspId}", method = RequestMethod.GET)
    public UpdateInformationDTO updates(@PathVariable String cspId) {
        return confService.updates(cspId);
    }

    @RequestMapping(value = "/v" + REST_API_V1 +  API_REGISTER + "/{cspId}", method = RequestMethod.POST)
    public ResponseDTO register(@PathVariable String cspId, @RequestBody RegistrationDTO cspRegistration) {
        return confService.register(cspId,cspRegistration);
    }

    @RequestMapping(value = "/v" + REST_API_V1 + API_UPDATE + "/{cspId}" + "/{updateHash}", method = RequestMethod.GET)
    public ResponseEntity update(@PathVariable String cspId, @PathVariable String updateHash) {
        return confService.update(cspId,updateHash);
    }

    @RequestMapping(value = "/v" + REST_API_V1 +  API_APPINFO + "/{cspId}", method = RequestMethod.POST)
    public void appInfo(@PathVariable String cspId, @RequestBody AppInfoDTO appInfo) {
        confService.appInfo(cspId,appInfo);
    }

}
