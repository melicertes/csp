package com.instrasoft.csp.ccs.domain.postgresql;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name="csp_info")
public class CspInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="csp_info_id_seq")
    @SequenceGenerator(name="csp_info_id_seq", sequenceName="csp_info_id_seq", allocationSize=1)
    @Column(name="id")
    private Long id;

    @Column(name="csp_id")
    @NotNull
    private String cspId;

    @Column(name="record_date_time", columnDefinition="timestampt with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date recordDateTime;


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

    public Date getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(Date recordDateTime) {
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
