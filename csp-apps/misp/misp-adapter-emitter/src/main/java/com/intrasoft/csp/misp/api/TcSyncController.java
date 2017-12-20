package com.intrasoft.csp.misp.api;

import com.intrasoft.csp.misp.commons.config.ApiContextUrl;
import com.intrasoft.csp.misp.service.MispTcSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TcSyncController implements ApiContextUrl{
    private static final Logger LOG = LoggerFactory.getLogger(TcSyncController.class);

    @Autowired
    MispTcSyncService mispTcSyncService;

    @ResponseBody
    @RequestMapping(value = API_BASE + "/v" + REST_API_V1 + "/" + TC_SYNC_ALL, method = RequestMethod.GET)
    public void tcSyncAlls() {
        LOG.info("MISP Endpoint: Get received - "+TC_SYNC_ALL);
        mispTcSyncService.syncAll();
    }

    @ResponseBody
    @RequestMapping(value = API_BASE + "/v" + REST_API_V1 + "/" + TC_SYNC_ORGANIZATIONS, method = RequestMethod.GET)
    public void tcSyncOrgs() {
        LOG.info("MISP Endpoint: Get received - "+TC_SYNC_ORGANIZATIONS);
        mispTcSyncService.syncOrganisations();
    }

    @ResponseBody
    @RequestMapping(value = API_BASE + "/v" + REST_API_V1 + "/" + TC_SYNC_SHARING_GROUPS, method = RequestMethod.GET)
    public void tcSyncGroups() {
        LOG.info("MISP Endpoint: Get received - "+TC_SYNC_SHARING_GROUPS);
        mispTcSyncService.syncSharingGroups();
    }
}
