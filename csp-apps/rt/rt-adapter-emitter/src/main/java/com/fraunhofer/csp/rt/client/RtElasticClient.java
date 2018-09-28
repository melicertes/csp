package com.fraunhofer.csp.rt.client;

import java.io.IOException;

import com.intrasoft.csp.client.ElasticClient;
import com.intrasoft.csp.commons.model.IntegrationDataType;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public interface RtElasticClient extends ElasticClient {
	public boolean objectExists(String uuid, IntegrationDataType datatype) throws IOException;
}
