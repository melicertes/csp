package com.instasoft.csp.ccs.domain.api;


public class Response {

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


    public Response(Integer responseCode, String responseText) {
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
