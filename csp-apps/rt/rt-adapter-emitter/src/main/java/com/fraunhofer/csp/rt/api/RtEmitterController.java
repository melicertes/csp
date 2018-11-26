package com.fraunhofer.csp.rt.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fraunhofer.csp.rt.app.RtAppClient;
import com.fraunhofer.csp.rt.service.EmitterDataHandler;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@RestController
public class RtEmitterController {
	final Logger LOG = LoggerFactory.getLogger(RtEmitterController.class);

	@Autowired
	EmitterDataHandler emitterDataHandler;

	@Autowired
	RtAppClient rtAppClient;

	@RequestMapping(value = "/rt/emitter/{ticketid}", method = RequestMethod.GET, headers = "Accept=application/json")
	public void getTicketById(@PathVariable String ticketid) {
		LOG.info("RT EMITTER Endpoint: GET received");
		try {
			emitterDataHandler.handleReemittionRtData(ticketid, false);
		} catch (IOException e) {
			LOG.error("getTicketById failed with Error: ", e);
		}
	}

	@RequestMapping(value = "/rt/emitter/test/{ticketid}", method = RequestMethod.GET, headers = "Accept=application/json")
	public void getTestTicketById(@PathVariable String ticketid) {
		LOG.info("RT TEST EMITTER Endpoint: GET received");
		emitterDataHandler.handleRtTestData(ticketid, false);
	}

}
