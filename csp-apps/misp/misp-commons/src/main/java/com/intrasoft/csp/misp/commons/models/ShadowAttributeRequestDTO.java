package com.intrasoft.csp.misp.commons.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ShadowAttributeRequestDTO {

    @JsonProperty("request")
    AttributeRequest request;

    public ShadowAttributeRequestDTO() {
    }

    public ShadowAttributeRequestDTO(AttributeRequest request) {
        this.request = request;
    }

    public AttributeRequest getRequest() {
        return request;
    }

    public void setRequest(AttributeRequest request) {
        this.request = request;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShadowAttributeRequest{");
        sb.append("request=").append(request);
        sb.append('}');
        return sb.toString();
    }

    public static class AttributeRequest {

        @JsonProperty("ShadowAttribute")
        private Object shadowAttribute;

        public AttributeRequest() {
        }

        public AttributeRequest(Object shadowAttribute) {
            this.shadowAttribute = shadowAttribute;
        }

        public Object getShadowAttribute() {
            return shadowAttribute;
        }

        public void setShadowAttribute(Object shadowAttribute) {
            this.shadowAttribute = shadowAttribute;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("AttributeRequest{");
            sb.append("shadowAttribute=").append(shadowAttribute);
            sb.append('}');
            return sb.toString();
        }
    }
}
