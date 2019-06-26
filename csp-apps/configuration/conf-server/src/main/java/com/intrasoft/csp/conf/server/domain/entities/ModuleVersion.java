package com.intrasoft.csp.conf.server.domain.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="module_version", uniqueConstraints = { @UniqueConstraint(columnNames = {"hash"})})
public class ModuleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="module_id")
    @NotNull
    private Long moduleId;

    @Column(name="full_name")
    @NotNull
    private String fullName;

    @Column(name="version")
    @NotNull
    private Integer version;

    @Column(name="released_on")
    @NotNull
    private String releasedOn;

    @Column(name="hash", unique = true)
    @NotNull
    private String hash;

    @Column(name="description")
    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getReleasedOn() {
        return releasedOn;
    }

    public void setReleasedOn(String releasedOn) {
        this.releasedOn = releasedOn;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ModuleVersion{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", fullName='" + fullName + '\'' +
                ", version=" + version +
                ", releasedOn='" + releasedOn + '\'' +
                ", hash='" + hash + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
