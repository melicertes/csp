package com.intrasoft.csp.misp.commons.config;


public interface MispContextUrl {

    public static String MISP_EVENT = "misp_json";
    public static String MISP_EVENT_DELETE = "misp_json_event";
    public static String MISP_ATTRIBUTE = "misp_json_attribute";
    public static String MISP_DELETE = "delete";
    public static String MISP_HEARTBEAT = "misp_json_self";
    public static String MISP_EVENTS = "events";

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

    public static enum RTIREntity {
        APPLICATION_ID("rt"),
        RTIR_NAME("\"rtir\""),

        MAP_CSP_FIELDS("\"classification\""),
        MAP_CSP_URL_VALUE("\"csp::url\""),
        MAP_ORIGIN_CSP_ID_VALUE("\"csp::originCspId\""),
        MAP_ORIGIN_RECORD_ID_VALUE("\"csp::originRecordId\""),

        MAP_TITLE("\"subject\""),
        MAP_TICKET_NO("\"ticket-number\"");


        private final String value;

        RTIREntity(String value) {
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }

}
