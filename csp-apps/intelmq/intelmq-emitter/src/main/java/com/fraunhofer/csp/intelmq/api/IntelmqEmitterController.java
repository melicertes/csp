package com.fraunhofer.csp.intelmq.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fraunhofer.csp.intelmq.service.EmitterDataHandler;

/**
 * Created by Majid Salehi on 20/04/2018
 */
@RestController
public class IntelmqEmitterController {

	private static final String INTELMQ_V1_EMITTER = "/intelmq/v1/emitter";

	final Logger LOG = LoggerFactory.getLogger(IntelmqEmitterController.class);

	@Autowired
	EmitterDataHandler emitterDataHandler;

	@RequestMapping(value = INTELMQ_V1_EMITTER, consumes = { "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<String> synchNewIntelmqData(@RequestBody String intelmqEventData) {
		LOG.info("INTELMQ Endpoint: POST received");
		return emitterDataHandler.handleIntelmqData(intelmqEventData, "POST");
	}

}
