package com.intrasoft.csp.model;

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

    @JsonProperty("action")
    private String action;

    @JsonProperty("fieldtype")
    private String fieldType;

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
        sb.append(", field='").append(field).append('\'');
        sb.append(", action=").append(action);
        sb.append(", fieldType=").append(fieldType);
        sb.append('}');
        return sb.toString();
    }

    private enum ActionType {
        ANON,
        PSEUDO
    }

    private enum FieldType {
        DATE,
        IP,
        EMAIL
    }
}