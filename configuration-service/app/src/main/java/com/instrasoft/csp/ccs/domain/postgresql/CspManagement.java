package com.instrasoft.csp.ccs.domain.postgresql;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="csp_management")
public class CspManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="csp_management_id_seq")
    @SequenceGenerator(name="csp_management_id_seq", sequenceName="csp_management_id_seq", allocationSize=1)
    @Column(name="id")
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


    @Override
    public String toString() {
        return "CspModule{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", moduleId=" + moduleId +
                ", moduleVersionId=" + moduleVersionId +
                '}';
    }
}
