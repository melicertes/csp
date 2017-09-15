package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

import javax.persistence.*;

/**
 * Created by tangelatos on 11/09/2017.
 */
@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @ToString
@Builder
public class SystemService {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @Column(columnDefinition = "VARCHAR(60)", unique = true)
    String name;


    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(16)")
    ServiceState serviceState;

    @ManyToOne(fetch = FetchType.EAGER)
    SystemModule module;

    @Column(columnDefinition = "SMALLINT")
    Boolean startable;

}
