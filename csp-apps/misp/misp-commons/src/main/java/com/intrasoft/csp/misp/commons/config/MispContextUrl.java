package com.intrasoft.csp.misp.commons.config;


public interface MispContextUrl {

    public static String MISP_EVENT = "misp_json";
    public static String MISP_AUDIT = "misp_json_audit";
    public static String MISP_EVENT_DELETE = "misp_json_event";
    public static String MISP_ATTRIBUTE = "misp_json_attribute";
    public static String MISP_DELETE = "delete";
    public static String MISP_HEARTBEAT = "misp_json_self";
    public static String MISP_EVENTS = "events";
    public static String MISP_ORGANISATIONS_VIEW = "organisations/view";
    public static String MISP_ORGANISATIONS_VIEW_ALL_ON_INSTANCE = "organisations/index";
    public static String MISP_ORGANISATIONS_VIEW_ALL_LOCAL = "organisations/index/scope:local";
    public static String MISP_ORGANISATIONS_VIEW_ALL_EXTERNAL = "organisations/index/scope:external";
    public static String MISP_ORGANISATIONS_VIEW_ALL_LOCAL_AND_EXTERNAL = "organisations/index/scope:all";
    public static String MISP_ORGANISATIONS_ADD = "admin/organisations/add";
    public static String MISP_ORGANISATIONS_EDIT = "admin/organisations/edit";
    public static String MISP_ORGANISATIONS_DELETE = "admin/organisations/delete";
    public static String MISP_SHARINGGROUPS_VIEW_ALL = "sharing_groups";
    public static String MISP_SHARINGGROUPS_VIEW_ALL_PASSIVE = "sharing_groups/index/true";
    public static String MISP_SHARINGGROUPS_VIEW = "sharing_groups/view";
    public static String MISP_SHARINGGROUPS_ADD = "sharing_groups/add";
    public static String MISP_SHARINGGROUPS_EDIT = "sharing_groups/edit";
    public static String MISP_SHARINGGROUPS_DELETE = "sharing_groups/delete";
    public static String MISP_SHARINGGROUPS_ADD_ORGANISATION = "/sharingGroups/addOrg";
    public static String MISP_SHARINGGROUPS_REMOVE_ORGANISATION = "/sharingGroups/removeOrg";
    public static String MISP_SHARINGGROUPS_ADD_SERVER = "/sharingGroups/addServer";
    public static String MISP_SHARINGGROUPS_REMOVE_SERVER = "/sharingGroups/removeServer";



    public static enum MispEntity {
        EVENT("Event"),
        ACTION("action"),
        ATTRIBUTE("Attribute"),
        ORGANISATION("Organisation"),
        SHARINGGROUP("SharingGroup");

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
        RTIR_NAME("csp-rtir"),

        /*
        CSP fields to look for in "object_relation" node
         */
        MAP_CSP_URL_VALUE("csp-url"),
        MAP_ORIGIN_CSP_ID_VALUE("csp-originCspId"),
        MAP_ORIGIN_RECORD_ID_VALUE("csp-originRecordId"),

        /*
        TITLE
         */
        MAP_TITLE("subject"),

        /*
        Ticket Number
         */
        MAP_TICKET_NO("ticket-number");

        private final String value;

        RTIREntity(String value) {
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }

    public static enum VULNERABILITYEntity {
        APPLICATION_ID("taranis"),
        VULNERABILITY_NAME("csp-vulnerability"),

        /*
        CSP fields to look for in "object_relation" node
         */
        MAP_CSP_URL_VALUE("csp-url"),
        MAP_ORIGIN_CSP_ID_VALUE("csp-originCspId"),
        MAP_ORIGIN_RECORD_ID_VALUE("csp-originRecordId"),

        /*
        Title
         */
        MAP_TITLE_RELATION("text"),
        MAP_TITLE_CATEGORY("Other"),

        /*
        ID
         */
        MAP_RECORD_RELATION("id"),
        MAP_RECORD_CATEGORY("External analysis");

        private final String value;

        VULNERABILITYEntity(String value) {
            this.value = value;
        }

        public String toString(){
            return this.value;
        }
    }

}
