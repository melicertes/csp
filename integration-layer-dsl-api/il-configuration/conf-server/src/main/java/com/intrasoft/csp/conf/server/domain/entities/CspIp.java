package com.intrasoft.csp.conf.server.domain.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name="csp_ip")
public class CspIp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="csp_id")
    @NotNull
    private String cspId;

    @Column(name="ip")
    @NotNull
    private String ip;

    @Column(name="external")
    @NotNull
    private Integer external;


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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getExternal() {
        return external;
    }

    public void setExternal(Integer external) {
        this.external = external;
    }

    @Override
    public String toString() {
        return "CspIp{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", ip='" + ip + '\'' +
                ", external=" + external +
                '}';
    }
}
