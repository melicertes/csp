package com.intrasoft.csp.anon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.anon.model.Rule;
import com.intrasoft.csp.anon.model.Rules;
import com.intrasoft.csp.anon.utils.HMAC;
import com.intrasoft.csp.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.commons.model.IntegrationAnonData;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.commons.validators.IntegrationDataValidator;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

@Service
public class ApiDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDataHandler.class);

    @Autowired
    RulesService rulesService;

    @Autowired
    HMAC hmac;

    private static ObjectMapper mapper = new ObjectMapper();

    private static final Configuration configuration = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    public ResponseEntity<?> handleAnonIntegrationData(IntegrationAnonData integrationAnonData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        String cspId = integrationAnonData.getCspId();
        IntegrationDataType dataType = integrationAnonData.getDataType();
        if (dataType == null){
            throw new InvalidDataTypeException("Unknown Datatype");
        }

//        String jsonString = mapper.writeValueAsString(integrationAnonData.getDataObject());
//        JsonNode out = JsonPath.using(configuration).parse(jsonString).json();
        JsonNode out = integrationAnonData.getDataObject();

        Rules rules = rulesService.getRule(dataType, cspId);

        if (rules == null){
            ObjectMapper mapper = new ObjectMapper();

            return new ResponseEntity<>(mapper.writeValueAsString(integrationAnonData.getDataObject()),
                    HttpStatus.OK);
        }

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
        LOG.info(mapper.writeValueAsString(integrationAnonData));
        return new ResponseEntity<>(integrationAnonData, HttpStatus.OK);
    }

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

    private String anonField(String val){
        return "*********";
    }

    private String pseudoField(String val) throws NoSuchAlgorithmException, InvalidKeyException {

        String secret = hmac.getKey().getKey();
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(val.getBytes()));
        return hash;
    }
}
