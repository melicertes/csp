package com.intrasoft.csp.misp.service;

public enum DistributionPolicy {

    YOUR_ORGANISATION_ONLY(1, "Your organisation only"),
    THIS_COMMUNITY_ONLY(2, "This community only"),
    CONNECTED_COMMUNITIES(3, "Connected communities"),
    ALL_COMMUNITIES(4, "All communities"),
    SHARING_GROUP(5, "Sharing Group");

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
