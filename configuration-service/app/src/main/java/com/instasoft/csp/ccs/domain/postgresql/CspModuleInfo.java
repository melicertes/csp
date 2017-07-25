package com.instasoft.csp.ccs.domain.postgresql;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "csp_module_info")
public class CspModuleInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="csp_module_info_seq")
    @SequenceGenerator(name="csp_module_info_seq", sequenceName="csp_module_info_seq", allocationSize=1)
    @Column(name="id")
    private Long id;

    @Column(name="csp_info_id")
    @NotNull
    private Long cspInfoId;

    @Column(name="module_version_id")
    @NotNull
    private Long moduleVersionId;

    @Column(name="module_installed_on")
    @NotNull
    private String moduleInstalledOn;

    @Column(name="module_is_active")
    @NotNull
    private Integer moduleIsActive;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCspInfoId() {
        return cspInfoId;
    }

    public void setCspInfoId(Long cspInfoId) {
        this.cspInfoId = cspInfoId;
    }

    public Long getModuleVersionId() {
        return moduleVersionId;
    }

    public void setModuleVersionId(Long moduleVersionId) {
        this.moduleVersionId = moduleVersionId;
    }

    public String getModuleInstalledOn() {
        return moduleInstalledOn;
    }

    public void setModuleInstalledOn(String moduleInstalledOn) {
        this.moduleInstalledOn = moduleInstalledOn;
    }

    public Integer getModuleIsActive() {
        return moduleIsActive;
    }

    public void setModuleIsActive(Integer moduleIsActive) {
        this.moduleIsActive = moduleIsActive;
    }


    @Override
    public String toString() {
        return "CspModuleInfo{" +
                "id=" + id +
                ", cspInfoId=" + cspInfoId +
                ", moduleVersionId=" + moduleVersionId +
                ", moduleInstalledOn='" + moduleInstalledOn + '\'' +
                ", moduleIsActive=" + moduleIsActive +
                '}';
    }
}
