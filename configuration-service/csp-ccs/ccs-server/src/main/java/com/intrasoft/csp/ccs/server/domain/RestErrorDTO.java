package com.intrasoft.csp.ccs.server.domain;


public class RestErrorDTO {

    private Integer responseCode;
    private String responseText;
    private String responseException;

    public RestErrorDTO() {}


    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseException() {
        return responseException;
    }

    public void setResponseException(String responseException) {
        this.responseException = responseException;
    }

    @Override
    public String toString() {
        return "RestErrorDTO{" +
                "responseCode=" + responseCode +
                ", responseText='" + responseText + '\'' +
                ", responseException='" + responseException + '\'' +
                '}';
    }
}
