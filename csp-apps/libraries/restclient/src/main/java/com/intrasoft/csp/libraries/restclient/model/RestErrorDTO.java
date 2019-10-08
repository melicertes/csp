package com.intrasoft.csp.libraries.restclient.model;

public class RestErrorDTO {
    
    private String exception;
    
    private String path;

    private String error;
    
    private String message;
    
    private String timestamp;
    
    private String status;

    private String name;
    private String errors;
    private String url;

    public RestErrorDTO() {
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "RestErrorDTO{" +
                "exception='" + exception + '\'' +
                ", path='" + path + '\'' +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", errors='" + errors + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
