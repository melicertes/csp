package com.intrasoft.csp.conf.server.domain.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="module")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", unique = true)
    @NotNull
    private String name;

    @Column(name="start_priority")
    @NotNull
    private Integer startPriority;

    @Column(name="is_default")
    @NotNull
    private Integer isDefault;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartPriority() {
        return startPriority;
    }

    public void setStartPriority(Integer startPriority) {
        this.startPriority = startPriority;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }


    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startPriority=" + startPriority +
                ", isDefault=" + isDefault +
                '}';
    }
}
