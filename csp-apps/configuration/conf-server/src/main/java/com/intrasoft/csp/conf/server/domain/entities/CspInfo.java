package com.intrasoft.csp.conf.server.domain.entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="csp_info")
public class CspInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="csp_id")
    @NotNull
    private String cspId;

    @Column(name="record_date_time")
    @NotNull
    private String recordDateTime;


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

    public String getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(String recordDateTime) {
        this.recordDateTime = recordDateTime;
    }


    @Override
    public String toString() {
        return "CspInfo{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", recordDateTime=" + recordDateTime +
                '}';
    }
}
