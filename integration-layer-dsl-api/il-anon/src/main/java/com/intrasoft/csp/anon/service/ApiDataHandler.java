package com.intrasoft.csp.anon.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.anon.utils.HMAC;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.intrasoft.csp.anon.model.IntegrationAnonData;
import com.intrasoft.csp.anon.model.Rule;
import com.intrasoft.csp.anon.model.Rules;
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
import java.util.*;

@Service
public class ApiDataHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiDataHandler.class);

    @Autowired
    RulesService rulesService;

    @Autowired
    HMAC hmac;

    private static ObjectMapper mapper = new ObjectMapper();


    public ResponseEntity<String> handleAnonIntegrationData(IntegrationAnonData integrationAnonData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        String cspId = integrationAnonData.getCspId();
        IntegrationDataType dataType = integrationAnonData.getDataType();

        String jsonString = mapper.writeValueAsString(integrationAnonData.getIntegrationData().getDataObject());
        JsonNode root = (ObjectNode)mapper.readTree(jsonString.getBytes());

        Rules rules = rulesService.getRule(dataType, cspId);

        if (rules == null){
            ObjectMapper mapper = new ObjectMapper();
            return new ResponseEntity<String>(mapper.writeValueAsString(integrationAnonData.getIntegrationData()),
                    HttpStatus.OK);
        }

        for (Rule rule : rules.getRules()){
            List<String> fields = Arrays.asList(rule.getField().toLowerCase().split("\\."));
            JsonNode locatedNode = root;
            int i;
            for (i = 0; i < fields.size() - 1; ++i){
                locatedNode = (ObjectNode)locatedNode.path(fields.get(i));
            }
            String fieldKey = fields.get(i);
            if (rule.getAction().toLowerCase().equals("pseudo")){
                ((ObjectNode) locatedNode).put(fieldKey, pseudoField(locatedNode.path(fields.get(i)).asText()));
            }
            else if (rule.getAction().toLowerCase().equals("anon")){

                ((ObjectNode) locatedNode).put(fieldKey, anonField(locatedNode.path(fields.get(i)).asText()));
            }
        }


        return new ResponseEntity<String>(mapper.writeValueAsString(root),
                HttpStatus.OK);
    }

    private void parse(String json) throws IOException {
        JsonFactory factory = new JsonFactory();

        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(json);

        Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
        while (fieldsIterator.hasNext()) {

            Map.Entry<String,JsonNode> field = fieldsIterator.next();
            this.parse(field.getValue().asText());
        }
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
