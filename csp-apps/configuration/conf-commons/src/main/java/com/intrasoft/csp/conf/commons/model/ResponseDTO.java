package com.intrasoft.csp.conf.commons.model;


public class ResponseDTO {

    private Integer responseCode;
    private String responseText;


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

    public ResponseDTO() {
        super();
    }

    public ResponseDTO(Integer responseCode, String responseText) {
        this.responseCode = responseCode;
        this.responseText = responseText;
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseCode=" + responseCode +
                ", responseText='" + responseText + '\'' +
                '}';
    }
}
