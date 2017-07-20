package com.intrasoft.csp.service;

import com.intrasoft.csp.api.HttpStatusResponseType;
import com.intrasoft.csp.model.IntegrationAnonData;
import com.intrasoft.csp.model.IntegrationDataType;
import com.intrasoft.csp.model.Rule;
import com.intrasoft.csp.model.Rules;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ApiDataHandler {

    @Autowired
    RulesProcessor rulesProcessor;


    public ResponseEntity<String> handleAnonIntegrationData(IntegrationAnonData integrationAnonData) throws NoSuchAlgorithmException, InvalidKeyException {

        // @TODO Handle integrationData, send IntegrationData to the anonymization service and receive anonymized intagrationData

        String cspId = integrationAnonData.getCspId();
        IntegrationDataType dataType = integrationAnonData.getDataType();
        byte[] File = integrationAnonData.getFile();

        Rules rules = rulesProcessor.getRule(dataType, cspId);

        for (Rule rule : rules.getRules()){

            if (rule.getAction().equals("PSEUDO")){



            }
            else if (rule.getAction().equals("ANON")){

            }
        }


        return new ResponseEntity<String>(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase(),
                HttpStatus.OK);
    }

    // @TODO Implement anonymize
    private String anonField(String val){

        return val;
    }

    // @TODO Implement pseudomize
    private String pseudoField(String val) throws NoSuchAlgorithmException, InvalidKeyException {

        String secret = "secret";
        String message = "Message";

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(message.getBytes()));
        System.out.println(hash);
        return val;
    }
}
