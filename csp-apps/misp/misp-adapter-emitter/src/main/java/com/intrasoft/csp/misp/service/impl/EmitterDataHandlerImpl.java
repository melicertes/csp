package com.intrasoft.csp.misp.service.impl;

import com.intrasoft.csp.commons.model.DataParams;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.model.SharingParams;
import com.intrasoft.csp.misp.service.EmitterDataHandler;
import org.springframework.stereotype.Service;

@Service
public class EmitterDataHandlerImpl implements EmitterDataHandler {
    @Override
    public void handleMispData(String content) {

        DataParams dataParams = new DataParams();

        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(true);

        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataParams(dataParams);
        integrationData.setSharingParams(sharingParams);
        integrationData.setDataObject(content);

        //TODO post integartionData to DSL of IntegrationLayer

    }
}
