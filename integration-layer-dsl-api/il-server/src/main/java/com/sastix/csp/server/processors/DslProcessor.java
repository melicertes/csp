package com.sastix.csp.server.processors;

import com.sastix.csp.commons.constants.AppProperties;
import com.sastix.csp.commons.model.IntegrationData;
import com.sastix.csp.commons.model.IntegrationDataType;
import com.sastix.csp.commons.routes.CamelRoutes;
import com.sastix.csp.commons.routes.HeaderName;
import com.sastix.csp.server.config.Flow1ApplicationsUrls;
import com.sastix.csp.server.config.Flow2ApplicationsUrls;
import com.sastix.csp.server.service.CspUtils;
import org.apache.camel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DslProcessor implements Processor {

    @Produce
    ProducerTemplate producerTemplate;

    @Autowired
    CspUtils cspUtils;

    @Autowired
    Environment env;

    private static final Logger LOG = LoggerFactory.getLogger(DslProcessor.class);

    private Flow1ApplicationsUrls flow1ApplicationsUrls;
    private Flow2ApplicationsUrls flow2ApplicationsUrls;

    @Deprecated
    @Autowired
    public void setFlow1ApplicationsUrls(Flow1ApplicationsUrls flow1ApplicationsUrls) {
        this.flow1ApplicationsUrls = flow1ApplicationsUrls;
    }

    @Deprecated
    @Autowired
    public void setFlow2ApplicationsUrls(Flow2ApplicationsUrls flow2ApplicationsUrls) {
        this.flow2ApplicationsUrls = flow2ApplicationsUrls;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        IntegrationData integrationData = cspUtils.getExchangeData(exchange, IntegrationData.class);
        List<String> recipients = computeRecipientsApps(integrationData);
        exchange.getIn().setHeader("recipients", recipients);
    }

    private List<String> computeRecipientsApps(IntegrationData integrationData) {
        List<String> recipients = new ArrayList<>();
        IntegrationDataType dataType = integrationData.getDataType();
        Boolean isExternal = integrationData.getSharingParams().getIsExternal();

        List<String> apps = new ArrayList<>();
        String appsStr = env.getProperty((isExternal? AppProperties.EXTERNAL:AppProperties.INTERNAL)+"."+dataType.toString()+".apps");

        if(!StringUtils.isEmpty(appsStr)){
            String[] appsArr =  appsStr.split(",");
            apps = Arrays.asList(appsArr).stream().map(s->s.trim()).collect(Collectors.toList());
        }

        /* deprecated
        if (isExternal) {
            LOG.info("isExternal = {} => Flow 2 of integration layer (synch data from external CSPs), ", true);
            apps.addAll(flow2ApplicationsUrls.getAppsByDataType(dataType));
        } else {
            LOG.info("isExternal = {} => Flow 1 of integration layer (synch data from current CSP), ", false);
            recipients.add(CamelRoutes.DDL);
            apps.addAll(flow1ApplicationsUrls.getAppListByDataType(dataType));
        }*/

        if(!isExternal){
            recipients.add(CamelRoutes.DDL);
        }

        for (String app : apps) {
            //String uri = cspUtils.getAppUri(app);
            //recipients.add(uri + app);
            producerTemplate.sendBodyAndHeader(CamelRoutes.APP, ExchangePattern.InOut,integrationData, HeaderName.APP_NAME,app);
        }

        return recipients;
    }

}
