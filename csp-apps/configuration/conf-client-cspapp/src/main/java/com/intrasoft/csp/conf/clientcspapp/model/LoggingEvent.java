package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by tangelatos on 12/09/2017.
 */
@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode @ToString
public class LoggingEvent {

    @Id
    @Column(name = "timestmp")
    Long timestamp;

    @Column(name = "formatted_message")
    String formattedMessage;

    @Column(name = "level_string")
    String level;

}
