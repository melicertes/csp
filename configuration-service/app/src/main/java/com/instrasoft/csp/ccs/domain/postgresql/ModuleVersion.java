package com.instrasoft.csp.ccs.domain.postgresql;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="module_version")
public class ModuleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="module_version_id_seq")
    @SequenceGenerator(name="module_version_id_seq", sequenceName="module_version_id_seq", allocationSize=1)
    @Column(name="id")
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

    @Column(name="hash")
    @NotNull
    private String hash;


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


    @Override
    public String toString() {
        return "ModuleVersion{" +
                "id=" + id +
                ", moduleId=" + moduleId +
                ", fullName='" + fullName + '\'' +
                ", version=" + version +
                ", releasedOn='" + releasedOn + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
