package com.intrasoft.csp.commons.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.intrasoft.csp.commons.model.IntegrationData;
import net.openhft.hashing.LongHashFunction;

public class HmacHelper {
    private static final HmacHelper INSTANCE = new HmacHelper();

    private final ObjectMapper mapper;

    private HmacHelper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public static HmacHelper getInstance() {
        return INSTANCE;
    }

    public void hmacIntegrationData(IntegrationData data) {
        data.setHmac("xx");
        byte[] dataBuffer = new byte[0];
        try {
            dataBuffer = mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            data.setHmac("HMAC not available - "+e.getMessage());
        }
        data.setHmac(Long.toHexString(LongHashFunction.xx(997).hashBytes(dataBuffer)));
    }
}
