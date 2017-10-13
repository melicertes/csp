package com.intrasoft.csp.misp.adapter.controller;

import com.intrasoft.csp.libraries.versioning.model.ContextUrl;
import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(ApiContextUrl.API_BASE)
public class AdapterController implements ApiContextUrl, ContextUrl {

    @RequestMapping(value = "/v" + REST_API_V1 + API_TEST, method = RequestMethod.GET)
    public String test() {
        return "hello";
    }
}
