package com.intrasoft.csp.anon.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.server.model.Rule;
import com.intrasoft.csp.anon.server.model.Rules;
import com.intrasoft.csp.anon.server.utils.HMAC;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ApiDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDataHandler.class);

    @Autowired
    RulesService rulesService;

    @Autowired
    HMAC hmac;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String IPADDRESS_PATTERN =
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    private static ObjectMapper mapper = new ObjectMapper();

    private static final Configuration configuration = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    /**
     *
     * @param integrationAnonData
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public ResponseEntity<?> handleAnonIntegrationData(IntegrationAnonData integrationAnonData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        String cspId = integrationAnonData.getCspId();
        IntegrationDataType dataType = integrationAnonData.getDataType();
        if (dataType == null){
            throw new InvalidDataTypeException(HttpStatusResponseType.UNSUPPORTED_DATA_TYPE.getReasonPhrase());
        }

        if (cspId == null || cspId.equals("")){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase());
        }

        Rules rules = rulesService.getRule(dataType, cspId);
        if (rules == null){
            throw new MappingNotFoundForGivenTupleException(HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.getReasonPhrase());
        }

        JsonNode out = integrationAnonData.getDataObject();
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> jpointers = new HashMap<>();
        for (Rule rule : rules.getRules()){
            if (rule.getField().contains("[*]")){
                List<String> vals = JsonPath.using(configuration).parse(out).read(rule.getField(), new TypeRef<List<String>>(){});
                for (int i = 0; i < vals.size() ; i++){
                    String jpointer = rule.getField().replace("*",String.valueOf(i));
                    String action = rule.getAction();
                    String value = JsonPath.using(configuration).parse(out).read(jpointer, String.class);
                    out = JsonPath.using(configuration).parse(out).set(jpointer, updateField(action, value)).json();
                }
            }
            else {
                String jpointer = rule.getField();
                String action = rule.getAction();
                String value = JsonPath.using(configuration).parse(out).read(jpointer, String.class);
                out = JsonPath.using(configuration).parse(out).set(jpointer, updateField(action, value)).json();
            }
        }

        integrationAnonData.setDataObject(out);
        return new ResponseEntity<>(integrationAnonData, HttpStatus.OK);
    }

    /**
     *
     * @param action
     * @param fieldVal
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    private String updateField(String action, String fieldVal) throws InvalidKeyException, NoSuchAlgorithmException {
        String newVal = fieldVal;
        if (action.toLowerCase().equals("pseudo")){
            newVal = pseudoField(fieldVal);
        }
        else if (action.toLowerCase().equals("anon")){
            newVal = anonField(fieldVal);
        }
        return newVal;
    }

    /**
     *
     * @param fieldVal
     * @return
     */
    private String anonField(String fieldVal){
        String fieldtype = "string";

        Pattern rEmail = Pattern.compile(EMAIL_PATTERN);
        Pattern rIp = Pattern.compile(IPADDRESS_PATTERN);
        if (rEmail.matcher(fieldVal).matches()) fieldtype = "email";
        else if (rIp.matcher(fieldVal).matches()) fieldtype = "ip";

        switch (fieldtype) {
            case "string":
                return "*******";
            case "ip":
                return "***.***.***.***";
            case "email":
                return "***@******.**";
            case "alphanumeric":
                return "**********";
        }
        return "*********";
    }

    /**
     *
     * @param val
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private String pseudoField(String val) throws NoSuchAlgorithmException, InvalidKeyException {

        String secret = hmac.getKey().getKey();
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(val.getBytes()));
        return hash;
    }
}
