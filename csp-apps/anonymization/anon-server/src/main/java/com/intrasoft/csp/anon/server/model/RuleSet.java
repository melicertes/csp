package com.intrasoft.csp.anon.server.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by chris on 7/7/2017.
 */
@Entity
public class RuleSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    String filename;

    @NotNull
    @Column(length = 1024*1024*2)//2MB maximum
    byte[] file;

    String description;

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
        final StringBuilder sb = new StringBuilder("Ruleset{");
        sb.append("id=").append(id);
        sb.append(", filename='").append(filename).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
