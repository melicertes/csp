package com.instrasoft.csp.ccs.domain.postgresql;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="csp_contact")
public class CspContact {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="csp_contact_id_seq")
    @SequenceGenerator(name="csp_contact_id_seq", sequenceName="csp_contact_id_seq", allocationSize=1)
    @Column(name="id")
    private Long id;

    @Column(name="csp_id")
    @NotNull
    private String cspId;

    @Column(name="person_name")
    @NotNull
    private String personName;

    @Column(name="person_email")
    @NotNull
    private String personEmail;

    @Column(name="contact_type")
    @NotNull
    private String contactType;


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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }


    @Override
    public String toString() {
        return "CspContact{" +
                "id=" + id +
                ", cspId='" + cspId + '\'' +
                ", personName='" + personName + '\'' +
                ", personEmail='" + personEmail + '\'' +
                ", contactType='" + contactType + '\'' +
                '}';
    }

    public String toRow() {
        return this.personName + " / " + this.contactType + " (" + this.personEmail + ")";
    }
}
