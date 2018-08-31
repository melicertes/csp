package com.intrasoft.csp.anon.commons.model;

public enum ApplicationId {
    MISP("misp"),
    RT("rt"),
    INTELMQ("intelmq"),
    VIPER("viper"),
    TRUESTCIRCLE("trustcircle");

    private String applicationId;

    ApplicationId(String id){
        this.applicationId = id;
    }

    public String getApplicationId(){
        return applicationId;
    }

    public static ApplicationId asApplicationId(String str) {
        for (ApplicationId me : ApplicationId.values()) {
            if (me.name().equalsIgnoreCase(str))
                return me;
        }
        return null;
    }

}