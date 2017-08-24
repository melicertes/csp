package com.intrasoft.csp.server.policy.domain.model;

public enum SharingPolicyAction {
    SHARE_AS_IS(3), //highest priority
    SHARE_ANONYMIZED(2),
    DO_NOT_SHARE(1), //lowest priority
    NO_ACTION_FOUND(0);

    Integer priority;

    SharingPolicyAction(Integer priority) {
        this.priority = priority;
    }

    public int priority() {
        return this.priority;
    }
}
