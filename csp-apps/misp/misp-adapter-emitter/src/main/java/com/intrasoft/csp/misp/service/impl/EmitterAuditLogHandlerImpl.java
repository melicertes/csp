package com.intrasoft.csp.misp.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.intrasoft.csp.misp.service.EmitterAuditLogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.intrasoft.csp.misp.commons.config.MispContextUrl.MispEntity.EVENT;

@Service
public class EmitterAuditLogHandlerImpl implements EmitterAuditLogHandler {

    final private static Logger LOG = LoggerFactory.getLogger("misp-audit-log");

    @Override
    public void handleAuditLog(JsonNode object) throws IOException {
        /*
        "Log": {
            "model_id": "9",
            "description": "Event (9) updated by User \"admin@admin.test\" (1).",
            "action": "edit",
            "change": "info (2.1) => (2.2)",
            "changes": 1,
            "title": "Event (9): 2.2",
            "model": "Event",
            "user_id": "1",
            "email": "admin@admin.test",
            "org": "ORGNAME",
            "created": "2018-01-15 18:43:52"
        },
        "action": "log"
    }

    {
    "Log":{
        "org":"ORGNAME",
        "model":"User",
        "model_id":"1",
        "email":"admin@admin.test",
        "action":"login",
        "title":"User (1): admin@admin.test",
        "change":"",
        "created":"2018-01-15 18:50:53",
        "user_id":0,
        "description":""
        },
        "action":"log"
    }
    */
        LOG.info(object.toString());
        String msg;
        if (object.get("Log").has("action") && object.get("Log").has("title") && object.get("Log").has("change") &&
                object.get("Log").has("description") && object.get("Log").has("user_id") ){
            msg = object.get("Log").get("action").textValue() + ":";
            msg += object.get("Log").get("title").textValue() + ";";
            msg += object.get("Log").get("change").textValue() + ";";
            msg += object.get("Log").get("description").textValue() + ";";
            msg += object.get("Log").get("user_id").textValue() + ";";
            LOG.info(msg);
        }
        else {
            throw new RuntimeException("Unable to parse audit log from ZMQ");
        }
    }
}
