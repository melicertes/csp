package com.fraunhofer.csp.rt.service;

import java.io.IOException;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public interface EmitterDataHandler {

	public void handleRtData(String ticketid, boolean isDelete) throws IOException;

	public void handleReemittionRtData(String ticketid, boolean isDelete) throws IOException;

	public void handleRtTestData(String ticketid, boolean b);

}
