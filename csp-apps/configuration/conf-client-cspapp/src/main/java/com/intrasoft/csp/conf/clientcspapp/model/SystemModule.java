package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by tangelatos on 09/09/2017.
 */

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @ToString
@Builder
public class SystemModule implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "VARCHAR(60)")
    String name;

    @Column(columnDefinition = "VARCHAR(200)")
    String description;

    @Convert(converter = JpaConverterLocalDateTime.class)
    @Column(columnDefinition = "VARCHAR(23)")
    LocalDateTime installDate;

    @Column(columnDefinition = "SMALLINT")
    Boolean active;

    @Column(columnDefinition = "VARCHAR(10)")
    String version;

    @Column(columnDefinition = "TEXT")
    String archivePath;

    @Column(columnDefinition = "TEXT")
    String modulePath;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(12) default 'UNKNOWN'", nullable = false)
    ModuleState moduleState = ModuleState.UNKNOWN;

    @Column(columnDefinition = "TEXT")
    String hash;

    @Column(columnDefinition = "INTEGER")
    Integer startPriority;

    @Column(columnDefinition = "VARCHAR(2000)")
    String manifestJsonAsText;

}
