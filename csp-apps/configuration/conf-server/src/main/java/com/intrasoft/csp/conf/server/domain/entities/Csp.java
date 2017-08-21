package com.intrasoft.csp.conf.server.domain.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


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
