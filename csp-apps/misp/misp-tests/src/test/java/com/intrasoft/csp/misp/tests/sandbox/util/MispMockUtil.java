package com.intrasoft.csp.misp.tests.sandbox.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.intrasoft.csp.misp.commons.models.OrganisationDTO;
import com.intrasoft.csp.misp.commons.models.OrganisationWrapper;
import com.intrasoft.csp.misp.commons.models.generated.Response;
import com.intrasoft.csp.misp.commons.models.generated.ResponseAll;
import com.intrasoft.csp.misp.commons.models.generated.SharingGroup;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MispMockUtil {
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

    public static byte[] getJsonBytesForSharingGroupByUuid(URL url, String uuid) throws URISyntaxException, IOException {

        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        ResponseAll[] arr = mapper.readValue(json, ResponseAll[].class);
        ResponseAll responseAll = mapper.readValue(json, ResponseAll.class);
        List<Response> responseList = responseAll.getResponse();

//        List<Response> responseList = new ArrayList<>();
//        for (ResponseAll temp: arr) {
//            responseList.add((Response) temp.getResponse());
//        }
        Response response = responseList.stream().filter(r -> r.getSharingGroup()
                .getUuid().equals(uuid)).findAny().get();
        SharingGroup sharingGroup = response.getSharingGroup();
        return mapper.writeValueAsBytes(sharingGroup);
/*
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        TrustCircle[] arr = mapper.readValue(json,TrustCircle[].class);
        List<TrustCircle> list = Arrays.asList(arr);
        TrustCircle tc = list.stream().filter(t->t.getId().equals(uuid)).findAny().get();
        return mapper.writeValueAsBytes(tc);
*/
    }

    public static byte[] getJsonBytesForOrganisationByUuid(URL url, String uuid) throws URISyntaxException, IOException {

        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OrganisationWrapper[] arr = mapper.readValue(json, OrganisationWrapper[].class);
        List<OrganisationDTO> organisationDTOList = new ArrayList<>();
        for (OrganisationWrapper wrapper : arr) {
            organisationDTOList.add(wrapper.getOrganisation());
        }
        OrganisationDTO organisationDTO = organisationDTOList.stream()
                .filter(o -> o.getUuid().equals(uuid)).findAny().get();
        return mapper.writeValueAsBytes(organisationDTO);

/*
        String json = FileUtils.readFileToString(new File(url.toURI()), Charset.forName("UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Team[] arr = mapper.readValue(json,Team[].class);
        List<Team> list = Arrays.asList(arr);
        Team team = list.stream().filter(t->t.getId().equals(uuid)).findAny().get();
        return mapper.writeValueAsBytes(team);
*/
    }
}
