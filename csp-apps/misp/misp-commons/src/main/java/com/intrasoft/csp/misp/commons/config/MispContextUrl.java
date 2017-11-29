package com.intrasoft.csp.misp.commons.config;


public interface MispContextUrl {

    public static String MISP_EVENT = "misp_json";
    public static String MISP_EVENT_DELETE = "misp_json_event";
    public static String MISP_ATTRIBUTE = "misp_json_attribute";
    public static String MISP_DELETE = "delete";
    public static String MISP_HEARTBEAT = "misp_json_self";
    public static String MISP_EVENTS = "events";
    public static String MISP_ORGANISATIONS_VIEW = "organisations/view";
    public static String MISP_ORGANISATIONS_ADD = "admin/organisations/add";
    public static String MISP_ORGANISATIONS_EDIT = "admin/organisations/edit";
    public static String MISP_ORGANISATIONS_DELETE = "admin/organisations/delete";

    public static enum MispEntity {
        EVENT("Event"),
        ACTION("action"),
        ATTRIBUTE("Attribute"),
        ORGANISATION("Organisation");

        private final String value;

        MispEntity(String value) {
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }

    public static enum RTIREntity {
        RTIR_NAME("\"rtir\""),
        TITLE_CATEGORY("\"Internal reference\""),
        TITLE_RELATION("\"classification\""),
        URL_CATEGORY("\"Internal reference\""),
        URL_RELATION("\"Attribution\""),


        MAP_URL_CSPID("\"classification\""),
        MAP_URL_VALUE("\"csp::url\""),
        MAP_CSPID_VALUE("\"csp::originCspId\""),

        MAP_TITLE("\"subject\""),
        MAP_RECORDID("\"ticket-number\"");


        private final String value;

        RTIREntity(String value) {
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }

}
