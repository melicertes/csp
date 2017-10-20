package com.intrasoft.csp.anon.commons.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;

public class RuleSetDTO implements Serializable{

    private static final long serialVersionUID = -7745951309682259628L;

    private Long id;

    String filename;

    byte[] file;

    String description;

    public RuleSetDTO() {
    }

    /**
     * Constructor to update a new Mapping
     * */
    public RuleSetDTO(Long id, String filename, byte[] file, String description) {
        this.id = id;
        this.filename = filename;
        this.file = file;
        this.description = description;
    }

    /**
     * Constructor to create a new Mapping
     * */
    public RuleSetDTO(String filename, byte[] file, String description) {
        this.filename = filename;
        this.file = file;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RuleSetDTO{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
