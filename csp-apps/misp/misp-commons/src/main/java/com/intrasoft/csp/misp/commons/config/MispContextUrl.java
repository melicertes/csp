package com.intrasoft.csp.misp.commons.config;


public interface MispContextUrl {

    public static String MISP_EVENT = "misp_json";
    public static String MISP_ATTRIBUTE = "misp_json_attribute";
    public static String MISP_HEARTBEAT = "misp_json_self";
    public static String MISP_EVENTS = "/events";

    public static enum MispEntity {
        EVENT("Event"),
        ACTION("action"),
        ATTRIBUTE("Attribute");

        private final String value;

        MispEntity(String value) {
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }

}
