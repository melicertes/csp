package com.instrasoft.csp.ccs.config;


public enum HttpStatusResponseType {

    /*OK*/
    API_UPDATES_OK(0, "CSP updates list transmitted successfully", "<not used at this scope>"),
    API_UPDATES_NOT_FOUND(1000, "Transaction failure; cspId not found; failure to identify cspId presented", "<inherited from original exception>"),
    API_UPDATES_FAILURE(1999, "Transaction failure; Systemic failure", "<inherited from original exception>"),

    /*OK*/
    API_REGISTER_OK(0, "CSP registered successfully", "<not used at this scope>"),
    API_REGISTER_NOT_UPDATABLE(2000, "CSP registration failed", "CSP requests update with registrationIsUpdate = false"),
    API_REGISTER_INVALID_CSP_ENTRY(2001, "CSP registration failed", "CSP requests update but original entry does not exist OR cspId is not recognized"),
    API_REGISTER_INVALID_MODULE_NAME(2002, "CSP registration failed", "CSP request contains invalid module name"),
    API_REGISTER_INVALID_MODULE_VERSION(2003, "CSP registration failed", "CSP request contains invalid module version"),
    API_REGISTER_INVALID_MODULE_HASH(2004, "CSP registration failed", "CSP request contains invalid module hash"),
    API_REGISTER_FAILURE(2999, "Transaction failure; CSP is not registered due to system error", "<inherited from original exception>"),

    /*OK*/
    API_UPDATE_OK(0, "CSP update file sent successfully", "<not used at this scope>"),
    API_UPDATE_NOT_FOUND(3000, "Requested hash does not match a file", "<inherited from original exception>"),
    API_UPDATE_INVALID_CSP_ENTRY(3001, "Transaction failure", "CSP ID not found. Failure to identify cspId presented"),
    API_UPDATE_INVALID_HASH_ENTRY(3002, "Transaction failure", "The specified CSP ID is not eligible for the requested update"),
    API_UPDATE_FAILURE(3999, "Transaction failure", "<inherited from original exception>"),


    //API_APPINFO 4000


    //DASHBOARD AND MANAGE PAGES 5000
    DATA_DASHBOARD_MANAGE_OK(0, "CSP managed successfully", "<not used at this scope>"),
    DATA_DASHBOARD_500(5000, "Generic system error", "Failed to fetch data for page"),

    //CSP PAGES 6000
    DATA_CSP_SAVE_OK(0, "Successful operation. CSP information saved", "<not used at this scope>"),
    DATA_CSP_SAVE_RECORD_EXISTS(6000, "CSP ID provided already exists", "Data integrity violation"),
    DATA_CSP_SAVE_INCONSISTENT_CONTACT(6001, "CSP contacts are not stated correctly", "Data integrity violation"),
    DATA_CSP_DELETE_ERROR(6002, "CSP cannot be deleted", ""),
    DATA_CSP_DELETE_OK(0, "Successful operation. CSP information removed", ""),
    DATA_CSP_UPDATE_ERROR(6002, "CSP cannot be updated", ""),
    DATA_CSP_UPDATE_OK(0, "Successful operation. CSP information updated", ""),

    //MODULE PAGES 7000
    DATA_MODULE_SAVE_OK(0, "Successful operation. Module information saved", "<not used at this scope>"),
    DATA_MODULE_SAVE_UNIQUE(7000, "Error", "Short name already exists!"),
    DATA_MODULE_SAVE_EMPTY_FILE(7001, "Error", "Please select a ZIP file!"),
    DATA_MODULE_SAVE_HASH_FILE(7002, "Error", "File could not be saved!");


    private final int responseCode;
    private final String responseText;
    private final String responseException;

    HttpStatusResponseType(int code, String text, String exception) {
        this.responseCode = code;
        this.responseText = text;
        this.responseException = exception;
    }

    public int code() {
        return this.responseCode;
    }

    public String text() {
        return this.responseText;
    }

    public String exception() {
        return this.responseException;
    }
}
