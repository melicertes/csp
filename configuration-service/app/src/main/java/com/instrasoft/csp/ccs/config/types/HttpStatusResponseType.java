package com.instrasoft.csp.ccs.config.types;


public enum HttpStatusResponseType {

    /*
    API ERROR CODES
     */
    //API GENERIC
    API_OK(0, "Successful transaction", ""),
    API_FAILURE(999, "Transaction failure; Systemic failure", ""),
    API_INVALID_CSP_ENTRY(100, "Transaction failure; CSP ID not found. Failure to identify cspId presented", ""),
    API_INVALID_MODULE_NAME(101, "Transaction failure; Request contains invalid module name", ""),
    API_INVALID_MODULE_VERSION(102, "Transaction failure; Request contains invalid module version", ""),
    API_INVALID_MODULE_HASH(103, "Transaction failure; Request contains invalid module hash", ""),

    //API UPDATES SPECIFIC
    API_UPDATES_NOT_FOUND(200, "Transaction failure; cspId not found; failure to identify cspId presented", ""),

    //API REGISTER SPECIFIC
    API_REGISTER_NOT_UPDATABLE(300, "Transaction failure; CSP requests update with registrationIsUpdate = false", ""),

    //API UPDATE SPECIFIC
    API_UPDATE_NOT_FOUND(400, "Transaction failure; Requested hash does not match a file", ""),
    API_UPDATE_INVALID_CSP_ENTRY(401, "Transaction failure; CSP ID not found. Failure to identify cspId presented", ""),
    API_UPDATE_INVALID_HASH_ENTRY(402, "Transaction failure; The specified CSP ID is not eligible for the requested update", ""),

    //API_APPINFO SPECIFIC



    //DASHBOARD AND MANAGE PAGES 5000
    DATA_DASHBOARD_MANAGE_OK(0, "CSP managed successfully", ""),
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
    DATA_MODULE_SAVE_HASH_FILE(7002, "Error", "File could not be saved!"),
    DATA_MODULE_DELETE_ERROR(0, "Module Version cannot be removed", ""),
    DATA_MODULE_DELETE_OK(0, "Successful operation. Module Version information removed", "");

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
