package com.intrasoft.csp.anon.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intrasoft.csp.anon.commons.exceptions.AnonException;
import com.intrasoft.csp.anon.commons.exceptions.InvalidDataTypeException;
import com.intrasoft.csp.anon.commons.exceptions.MappingNotFoundForGivenTupleException;
import com.intrasoft.csp.anon.commons.model.ApplicationId;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.server.model.Rule;
import com.intrasoft.csp.anon.server.model.Rules;
import com.intrasoft.csp.anon.server.utils.CryptoPAN;
import com.intrasoft.csp.anon.server.utils.HMAC;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.IntegrationDataType;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

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
            .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS)
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

    public IntegrationAnonData handleAnonIntegrationData(IntegrationAnonData integrationAnonData) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        if(integrationAnonData == null){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()+"[integrationAnonData is null]");
        }

        LOG.debug("Handle integrationAnonData: " + integrationAnonData.toString());
        String cspId = integrationAnonData.getCspId();
        IntegrationDataType dataType = integrationAnonData.getDataType();
        String applicationIdString = integrationAnonData.getApplicationId();

        if (dataType == null){
            throw new InvalidDataTypeException(HttpStatusResponseType.UNSUPPORTED_DATA_TYPE.getReasonPhrase()+"[dataType is null]");
        }

        if (cspId == null || cspId.equals("")){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()+"[cspId is empty]");
        }

        if (applicationIdString == null || applicationIdString.equals("")){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()+"[applicationId is empty]");
        }

        if (applicationIdString == null || applicationIdString.equals("")){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()+"[applicationId is empty]");
        }

        ApplicationId applicationId = ApplicationId.asApplicationId(applicationIdString);

        if (applicationId == null){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase()+"[applicationId not valid]");
        }


        Rules rules = rulesService.getRule(dataType, cspId, applicationId);

        if (rules == null){
            LOG.debug("Ruleset mapping not found, using default.");
            throw new MappingNotFoundForGivenTupleException(HttpStatusResponseType.MAPPING_NOT_FOUND_FOR_GIVEN_TUPLE.getReasonPhrase()
                    +"[dataType: "+dataType+",cspId: "+cspId+ ", applicationId: " + applicationId.getApplicationId() + "]");
        }

        if (integrationAnonData.getDataObject() == null){
            throw new AnonException(HttpStatusResponseType.MALFORMED_INTEGRATION_DATA_STRUCTURE.getReasonPhrase());
        }

        JsonNode out = mapper.valueToTree(integrationAnonData.getDataObject());
        ReadContext ctx = JsonPath.using(configuration).parse(out);

        for (Rule rule : rules.getRules()){
            LOG.debug("Applying rule: " + rule.toString());
            List<LinkedHashMap> tmp = ctx.read(rule.getCondition(), List.class);
            for (LinkedHashMap jn : tmp){
                JsonNode jjn = new ObjectMapper().valueToTree(jn);

                String fieldVal = null;
                if (jjn.get(rule.getField()) != null){
                    fieldVal = jjn.get(rule.getField()).textValue();
                }

                /*Create Filter to match id*/
                Filter idFilter = null;
                JsonNode idNode = jjn.get("id");
                if (idNode != null){
                    idFilter = filter(
                            where("id").eq(idNode.asInt())
                    );
                }

                /*Get array with findings from rule condition*/
                ArrayNode test = JsonPath.using(configuration).parse(out).read(rule.getCondition());
                LOG.trace("Filtered per id: " + idNode.toString() + " --> "+ test.toString());

                /*Apply id filter on the each element of the rule condition's findings*/
                for (JsonNode arrEl : test){
                    String uuidCondition = "$.." + idFilter.toString();
                    LOG.trace(uuidCondition);
                    out = JsonPath.using(configuration).parse(out).set(uuidCondition,
                            ((ObjectNode)jjn).put(rule.getField(),updateField(rule.getAction(), rule.getFieldType(), fieldVal, dataType)),
                            idFilter).json();
                }

                /*out = JsonPath.using(configuration).parse(out).set(rule.getCondition(),
                        ((ObjectNode)jjn).put(rule.getField(),updateField(rule.getAction(), rule.getFieldType(), fieldVal, dataType)),
                        idFilter).json();*/
            }
        }

        integrationAnonData.setDataObject(out);
        return integrationAnonData;
    }

    /**
     *
     * @param action
     * @param fieldValue
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    private String updateField(String action, String fieldType, String fieldValue, IntegrationDataType dataType) throws InvalidKeyException, NoSuchAlgorithmException {
        String newVal = fieldValue;
        if (action.toLowerCase().equals("pseudo")){
            if (fieldType.equals("ip")){
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(hmac.getKey().getKey().getBytes());
//                String digest = new BigInteger(1, md.digest()).toString(16);
                String digest = String.format("%032x", new BigInteger(1, md.digest()));
                LOG.trace("CryptoPAN mask: " + digest);
                CryptoPAN cryptoPAN = new CryptoPAN(digest);
                newVal = cryptoPAN.anonymize(fieldValue);
                LOG.trace(fieldValue + " --> " + newVal);
            }
            else  {
                newVal = pseudoField(fieldValue);
            }

        }
        else if (action.toLowerCase().equals("anon")){
            newVal = anonField(fieldType, fieldValue, dataType);
        }
        return newVal;
    }

    /**
     *
     * @param fieldVal
     * @return
     */
    private String anonField(String fieldtype, String fieldVal, IntegrationDataType dataType){

        if (dataType.equals(IntegrationDataType.EVENT) ||
                dataType.equals(IntegrationDataType.THREAT) ||
                dataType.equals(IntegrationDataType.VULNERABILITY) ||
                dataType.equals(IntegrationDataType.ARTEFACT)){
            return "";
        }
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
                return "00000000";
            case "numeric":
                return "00000000";
            default:
                return "$$$$$$$$$";
        }
//        return "*********";
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
