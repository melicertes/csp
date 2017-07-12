package com.instrasoft.csp.ccs.config;


public enum HttpStatusResponseType {

    API_UPDATES_NOT_FOUND(1000, "Transaction failure; cspId not found; failure to identify cspId presented", ""),
    API_UPDATES_SYSTEM_FAILURE(1001, "Transaction failure; Systemic failure", ""),

    API_REGISTER_200(0, "Successful Submission", ""),
    API_REGISTER_400(2000, "CSP requests update with registrationIsUpdate = false", ""),
    API_REGISTER_404(2001, "CSP requests update but original entry does not exist OR cspId is not recognized", ""),
    API_REGISTER_500(2002, "Transaction failure; CSP is not registered due to system error", ""),

    //API_UPDATE 3000

    //API_APPINFO 4000


    //DASHBOARD AD MANAGE PAGES 5000
    DATA_DASHBOARD_500(5000, "Generic system error", "Failed to fetch data for page"),

    //CSP PAGES 6000
    DATA_CSP_SAVE_OK(0, "Successful operation. CSP information saved", ""),
    DATA_CSP_SAVE_RECORD_EXISTS(6000, "CSP ID provided already exists", "Data integrity violation"),
    DATA_CSP_SAVE_INCONSISTENT_CONTACT(6001, "CSP contacts are not stated correctly", "Data integrity violation"),
    DATA_CSP_DELETE_ERROR(6002, "CSP cannot be deleted", ""),
    DATA_CSP_DELETE_OK(0, "Successful operation. CSP information removed", ""),
    DATA_CSP_UPDATE_ERROR(6002, "CSP cannot be updated", ""),
    DATA_CSP_UPDATE_OK(0, "Successful operation. CSP information updated", ""),

    //MODULE PAGES 7000
    DATA_MODULE_SAVE_OK(0, "Successful operation. Module information saved", ""),
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
