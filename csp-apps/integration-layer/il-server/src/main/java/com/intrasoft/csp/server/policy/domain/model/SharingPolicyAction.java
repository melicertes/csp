package com.intrasoft.csp.server.policy.domain.model;

import com.intrasoft.csp.commons.model.IntegrationDataType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SharingPolicyAction {
    SHARE_AS_IS(3,"Share as is"), //highest priority
    SHARE_ANONYMIZED(2 ,"Share anonymized"),
    DO_NOT_SHARE(1, "Do not share"), //lowest priority
    NO_ACTION_FOUND(0, "No action found");

    Integer priority;
    String text;

    SharingPolicyAction(Integer priority, String text) {
        this.priority = priority;
        this.text = text;
    }

    public int priority() {
        return this.priority;
    }
    public String text() { return this.text; }

    public static final Map<IntegrationDataType,SharingPolicyAction> defaultActionMap;

    static{
        defaultActionMap = new ConcurrentHashMap<>();
        defaultActionMap.put(IntegrationDataType.DUMMY,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.THREAT,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.EVENT,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.ARTEFACT,SHARE_ANONYMIZED);
        defaultActionMap.put(IntegrationDataType.INCIDENT,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.CONTACT,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.FILE,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.CHAT,DO_NOT_SHARE);
        defaultActionMap.put(IntegrationDataType.VULNERABILITY,SHARE_AS_IS);
        defaultActionMap.put(IntegrationDataType.TRUSTCIRCLE,DO_NOT_SHARE);
    }
}
