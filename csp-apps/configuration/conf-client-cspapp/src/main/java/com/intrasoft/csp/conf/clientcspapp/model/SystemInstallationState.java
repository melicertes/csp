package com.intrasoft.csp.conf.clientcspapp.model;

import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @ToString
public class SystemInstallationState implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(36)")
    private String cspId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(12)")
    private InstallationState installationState = InstallationState.NOT_STARTED;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = RegistrationDTOJpaConverter.class)
    private RegistrationDTO cspRegistration;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = SmtpDetailsJpaConverter.class)
    private SmtpDetails smtpDetails;


}
