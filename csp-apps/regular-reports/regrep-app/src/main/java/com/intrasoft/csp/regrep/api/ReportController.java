package com.intrasoft.csp.regrep.api;

import com.intrasoft.csp.regrep.commons.config.ApiContextUrl;
import com.intrasoft.csp.regrep.service.Basis;
import com.intrasoft.csp.regrep.service.RegularReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ReportController implements ApiContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    RegularReportsService regularReportsService;

    // Must specify type of report on endpoint (.../daily, .../monthly etc)
    @ResponseBody
    @RequestMapping(value = API_BASE + "/v" + REST_API_V1 + "/" + REGREP_REPORT + "/{type}", method = GET)
    public void trigger(@PathVariable String type) {


        LOG.info("Regular Reports Endpoint: GET received " + REGREP_REPORT + "/" + type);
        if (validatePathVariable(type)) {
            regularReportsService.report(Basis.valueOf(type.toUpperCase()));
        } else {
            LOG.info("Must specify type of report on endpoint (daily, weekly, monthly etc)");
        }

    }

    private boolean validatePathVariable(String type) {
        for (Basis basis : Basis.values()) {
            if (basis.toString().toLowerCase().equals(type.toLowerCase()))
                return true;
        }
        return false;
    }

}
