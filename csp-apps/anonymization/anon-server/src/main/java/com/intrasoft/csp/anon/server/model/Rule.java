package com.intrasoft.csp.anon.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "field",
        "action",
        "fieldtype"
})
public class Rule {

    @JsonProperty("field")
    private String field;

    @JsonProperty("value")
    private String value;

    @JsonProperty("action")
    private String action;

    @JsonProperty("fieldtype")
    private String fieldType;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Rule() {
        super();
    }

    public Rule(String field, String value, String action, String fieldType) {
        this.field = field;
        this.value = value;
        this.action = action;
        this.fieldType = fieldType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Rule{");
        sb.append("field='").append(field).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append(", fieldType='").append(fieldType).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private enum ActionType {
        ANON,
        PSEUDO
    }

    /*
     * numbers are hashes #
     * chars are asterisks *
     * IP pseudonymization ###.###.###.###
     * EMAIL pseudonymization ****@********
     */
    private enum FieldType {
        IP,
        EMAIL,
        STRING,
        NUMBER
    }
}