package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

import javax.persistence.*;

/**
 * Created by tangelatos on 10/09/2017.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SmtpDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(50)")
    private String host;

    @Column(columnDefinition = "INT")
    private Integer port;

    @Column(columnDefinition = "VARCHAR(50)")
    private String userName;

    @Column(columnDefinition = "VARCHAR(50)")
    private String password;
    
}
