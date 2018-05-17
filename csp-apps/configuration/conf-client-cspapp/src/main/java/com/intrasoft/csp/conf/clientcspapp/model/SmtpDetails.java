package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

import javax.persistence.Column;

/**
 * Created by tangelatos on 10/09/2017.
 */

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SmtpDetails {

    @Column(columnDefinition = "VARCHAR(50)")
    private String host;

    @Column(columnDefinition = "INT")
    private Integer port;

    @Column(columnDefinition = "VARCHAR(50)")
    private String userName;

    @Column(columnDefinition = "VARCHAR(50)")
    private String password;

    @Column(columnDefinition = "VARCHAR(120)")
    private String senderName;

    @Column(columnDefinition = "VARCHAR(120)")
    private String senderEmail;
    
}
