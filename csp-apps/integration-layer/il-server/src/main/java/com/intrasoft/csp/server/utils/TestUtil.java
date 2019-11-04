package com.intrasoft.csp.server.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.intrasoft.csp.commons.model.IntegrationData;
import com.intrasoft.csp.commons.validators.HmacHelper;
import net.openhft.hashing.LongHashFunction;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by iskitsas on 4/4/17.
 */
public class TestUtil {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (object instanceof IntegrationData) {
            HmacHelper.getInstance().hmacIntegrationData((IntegrationData) object);
        }
        return mapper.writeValueAsBytes(object);
    }


}
