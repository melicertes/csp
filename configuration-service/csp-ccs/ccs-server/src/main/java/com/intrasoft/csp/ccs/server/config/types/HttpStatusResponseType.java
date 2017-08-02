package com.intrasoft.csp.ccs.server.config.types;


public enum HttpStatusResponseType {

    /*
    COMMON ERROR CODES
     */
    OK(0, "Successful transaction"),
    FAILURE(999, "Transaction failure; Systemic failure"),

    /*
    API ERROR CODES
     */

    //API GENERIC
    API_INVALID_CSP_ENTRY(100, "Transaction failure; CSP ID not found; Failure to identify cspId presented"),
    API_INVALID_MODULE_NAME(101, "Transaction failure; Request contains invalid module name"),
    API_INVALID_MODULE_VERSION(102, "Transaction failure; Request contains invalid module version"),
    API_INVALID_MODULE_HASH(103, "Transaction failure; Request contains invalid module hash"),

    //API UPDATES SPECIFIC


    //API REGISTER SPECIFIC
    API_REGISTER_NOT_UPDATABLE(300, "Transaction failure; CSP requests update with registrationIsUpdate = false"),

    //API UPDATE SPECIFIC
    API_UPDATE_NOT_FOUND(400, "Transaction failure; Requested hash does not match a file"),
    API_UPDATE_INVALID_HASH_ENTRY(402, "Transaction failure; The specified CSP ID is not eligible for the requested update"),

    //API_APPINFO SPECIFIC



    /*
    INTERNAL REST ERROR CODES:
     */

    //DATA GENERIC
    DATA_INVALID_CSP_ID(1000, "Transaction failure; CSP ID not found; Failure to identify cspId presented"),
    DATA_INVALID_MODULE_ID(1001, "Transaction failure; Module ID not found; Failure to identify moduleId presented"),
    DATA_INVALID_MODULE_VERSION_ID(1002, "Transaction failure; Module Version ID not found; Failure to identify moduleVersionId presented"),

    //DASHBOARD AND MANAGE PAGES
    DATA_DASHBOARD_MANAGE_OK(0, "CSP managed successfully"),

    //CSP PAGES
    DATA_CSP_SAVE_OK(0, "Successful transaction. CSP information saved"),
    DATA_CSP_UPDATE_OK(0, "Successful transaction. CSP information updated"),
    DATA_CSP_DELETE_OK(0, "Successful transaction. CSP information removed"),
    DATA_CSP_SAVE_RECORD_EXISTS(1100, "Transaction failure; Data integrity violation; CSP ID provided already exists"),
    DATA_CSP_SAVE_INVALID_CONTACT(1101, "Transaction failure; Data integrity violation; CSP contacts' data are not stated correctly"),

    //MODULE PAGES
    DATA_MODULE_SAVE_OK(0, "Successful operation. Module information saved"),
    DATA_MODULE_UPDATE_OK(0, "Successful transaction. Module information updated"),
    DATA_MODULE_DELETE_OK(0, "Successful transaction. Module information removed"),
    DATA_MODULE_SAVE_NAME_EXISTS(1200, "Transaction failure; Data integrity violation; Module Short Name already exists"),
    DATA_MODULE_DELETE_ERROR(1201, "Transaction failure; Module cannot be removed; Try to remove its Module Versions or Module is assigned to CSPs"),

    //MODULE VERSION PAGES
    DATA_MODULE_VERSION_SAVE_OK(0, "Successful operation. Module Version information saved"),
    DATA_MODULE_VERSION_UPDATE_OK(0, "Successful transaction. Module Version information updated"),
    DATA_MODULE_VERSION_DELETE_OK(0, "Successful transaction. Module Version information removed"),
    DATA_MODULE_VERSION_EMPTY_FILE(1300, "Transaction failure; File not found; Failure to identify file uploaded"),
    DATA_MODULE_VERSION_EXISTS(1301, "Transaction failure; Module Version already exists"),
    DATA_MODULE_VERSION_NAME_EXISTS(1302, "Transaction failure; Module Version full name already exists!"),
    DATA_MODULE_VERSION_HASH_EXISTS(1303, "Transaction failure; Module Version hash already exists!"),
    DATA_MODULE_VERSION_SAVE_FILE(1304, "Transaction failure; File could not be saved"),
    DATA_MODULE_VERSION_HASH_FILE(1305, "Transaction failure; Message digest of file could not be generated"),
    DATA_MODULE_VERSION_DELETE_ERROR(1306, "Transaction failure; Module Version cannot be removed; Module Version already assigned to CSPs"),
    DATA_MODULE_VERSION_INVALID_FILE(1307, "Transaction failure; Module Version file does not exists and cannot be removed");



    private final int responseCode;
    private final String responseText;

    HttpStatusResponseType(int code, String text) {
        this.responseCode = code;
        this.responseText = text;
    }

    public int code() {
        return this.responseCode;
    }

    public String text() {
        return this.responseText;
    }

}
