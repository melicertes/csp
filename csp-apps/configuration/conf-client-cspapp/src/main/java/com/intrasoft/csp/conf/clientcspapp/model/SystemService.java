package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;
import org.joda.time.LocalDateTime;

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

    @Column(columnDefinition = "SMALLINT")
    Boolean legacy;

    @Column(columnDefinition = "SMALLINT default '0'", nullable = false)
    Boolean oamAgentNecessary;

    @Column(columnDefinition = "SMALLINT default '0'", nullable = false)
    Boolean vHostNecessary;

    @Convert(converter = JpaConverterLocalDateTime.class)
    @Column(columnDefinition = "VARCHAR(23)")
    LocalDateTime oamAgentCreated;

    @Convert(converter = JpaConverterLocalDateTime.class)
    @Column(columnDefinition = "VARCHAR(23)")
    LocalDateTime vhostCreated;

}
