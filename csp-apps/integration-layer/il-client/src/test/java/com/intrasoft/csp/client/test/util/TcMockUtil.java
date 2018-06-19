package com.intrasoft.csp.client.test.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.intrasoft.csp.commons.model.Contact;
import com.intrasoft.csp.commons.model.Team;
import com.intrasoft.csp.commons.model.TrustCircle;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class TcMockUtil {
    public static String getJsonStrFromUrl(URL url) throws URISyntaxException, IOException {
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        return json;
    }

    public static byte[] getJsonBytesFromUrl(URL url) throws URISyntaxException, IOException {
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        mapper.registerModule(new JodaModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        Object jsonObj = mapper.readValue(json,Object.class);
//        return mapper.writeValueAsBytes(jsonObj);
        return json.getBytes();
    }

    public static byte[] getJsonBytesForTrustCircleByUuid(URL url, String uuid) throws URISyntaxException, IOException {
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        TrustCircle[] arr = mapper.readValue(json,TrustCircle[].class);
        List<TrustCircle> list = Arrays.asList(arr);
        TrustCircle tc = list.stream().filter(t->t.getId().equals(uuid)).findAny().get();
        return mapper.writeValueAsBytes(tc);
    }

    public static byte[] getJsonBytesForLTCByShortName(URL url, String shortName) throws URISyntaxException, IOException {
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        TrustCircle[] arr = mapper.readValue(json,TrustCircle[].class);
        List<TrustCircle> list = Arrays.asList(arr);

        //TrustCircle tc = list.stream().filter(t->t.getShortName().equals(shortName)).findAny().get();
        return mapper.writeValueAsBytes(list);
    }

    public static byte[] getJsonBytesForTeamByUuid(URL url, String uuid) throws URISyntaxException, IOException {
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Team[] arr = mapper.readValue(json,Team[].class);
        List<Team> list = Arrays.asList(arr);
        Team team = list.stream().filter(t->t.getId().equals(uuid)).findAny().get();
        return mapper.writeValueAsBytes(team);
    }

    public static byte[] getJsonBytesForContactById(URL url, String id) throws URISyntaxException, IOException {
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        Contact[] arr = mapper.readValue(json, Contact[].class);
        List<Contact> list = Arrays.asList(arr);
        Contact contact = list.stream().filter(c->c.getId().equals(id)).findAny().get();
        return mapper.writeValueAsBytes(contact);
    }
}
