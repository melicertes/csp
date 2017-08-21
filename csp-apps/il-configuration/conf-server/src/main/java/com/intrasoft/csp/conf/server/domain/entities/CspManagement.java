package com.intrasoft.csp.conf.server.domain.entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="csp_management")
public class CspManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="csp_id")
    @NotNull
    private String cspId;

    @Column(name="module_id")
    @NotNull
    private Long moduleId;

    @Column(name="module_version_id")
    @NotNull
    private Long moduleVersionId;

    @Column(name="date_changed")
    private String dateChanged;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCspId() {
        return cspId;
    }

    public void setCspId(String cspId) {
        this.cspId = cspId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getModuleVersionId() {
        return moduleVersionId;
    }

    public void setModuleVersionId(Long moduleVersionId) {
        this.moduleVersionId = moduleVersionId;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }


    @Override
    public String toString() {
        return "CspManagement{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", moduleId=" + moduleId +
                ", moduleVersionId=" + moduleVersionId +
                ", dateChanged='" + dateChanged + '\'' +
                '}';
    }
}
