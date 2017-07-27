package com.intrasoft.csp.ccs.server.domain.postgresql;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name="csp")
public class Csp {

    @Id
    @Column(name="id")
    private String id;

    @Column(name="name")
    @NotNull
    private String name;

    @Column(name="domain_name")
    @NotNull
    private String domainName;

    @Column(name="registration_date")
    //@Temporal(TemporalType.TIMESTAMP)
    private String registrationDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "Csp{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", domainName='" + domainName + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
