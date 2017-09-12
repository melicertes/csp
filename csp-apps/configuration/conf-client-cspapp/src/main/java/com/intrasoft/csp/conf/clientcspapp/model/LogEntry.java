package com.intrasoft.csp.conf.clientcspapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by tangelatos on 12/09/2017.
 */
@AllArgsConstructor
@Getter @Setter @ToString
public class LogEntry implements Serializable {

    String ts;
    String lt;
    String m;

}
