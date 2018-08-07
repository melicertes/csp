package com.intrasoft.csp.misp.service;

public enum DistributionPolicy {

    YOUR_ORGANISATION_ONLY(0, "Your organisation only"),
    THIS_COMMUNITY_ONLY(1, "This community only"),
    CONNECTED_COMMUNITIES(2, "Connected communities"),
    ALL_COMMUNITIES(3, "All communities"),
    SHARING_GROUP(4, "Sharing Group"),
    INHERIT_EVENT(5, "Inherit event");

    private final int level;
    private final String description;

    private DistributionPolicy(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DistributionPolicy{" +
                "level=" + level +
                ", description='" + description + '\'' +
                '}';
    }
}
