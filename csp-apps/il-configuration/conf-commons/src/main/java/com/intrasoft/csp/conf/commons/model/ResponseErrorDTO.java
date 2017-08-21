package com.intrasoft.csp.conf.commons.model;


public class ResponseErrorDTO {

    private Integer responseCode;
    private String responseText;
    private String responseException;

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


    public ResponseErrorDTO(Integer responseCode, String responseText, String responseException) {
        this.responseCode = responseCode;
        this.responseText = responseText;
        this.responseException = responseException;
    }

    @Override
    public String toString() {
        return "ResponseError{" +
                "responseCode=" + responseCode +
                ", responseText='" + responseText + '\'' +
                ", responseException='" + responseException + '\'' +
                '}';
    }
}
