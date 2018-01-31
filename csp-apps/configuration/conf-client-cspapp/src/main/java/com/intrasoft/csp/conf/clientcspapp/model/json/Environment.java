package com.intrasoft.csp.conf.clientcspapp.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "domain",
        "csp_name",
        "host_ip",
        "is_central",
        "internalSSLCertificateFile",
        "internalSSLCertificateKeyFile",
        "internalSSLCACertificateFile",
        "externalSSLCertificateFile",
        "externalSSLCertificateKeyFile",
        "externalSSLCACertificateFile",
        "postgres",
        "mail",
        "services"
})

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Environment {

    @JsonProperty("domain")
    public String domain;
    @JsonProperty("csp_name")
    public String cspName;
    @JsonProperty("host_ip")
    public String hostIp;
    @JsonProperty("is_central")
    public boolean isCentral;
    @JsonProperty("internalSSLCertificateFile")
    public String internalSSLCertificateFile;
    @JsonProperty("internalSSLCertificateKeyFile")
    public String internalSSLCertificateKeyFile;
    @JsonProperty("internalSSLCACertificateFile")
    public String internalSSLCACertificateFile;
    @JsonProperty("externalSSLCertificateFile")
    public String externalSSLCertificateFile;
    @JsonProperty("externalSSLCertificateKeyFile")
    public String externalSSLCertificateKeyFile;
    @JsonProperty("externalSSLCACertificateFile")
    public String externalSSLCACertificateFile;
    @JsonProperty("postgres")
    @Valid
    public Postgres postgres;
    @JsonProperty("mail")
    @Valid
    public Mail mail;
    @JsonProperty("services")
    @Valid
    public List<Service> services = new ArrayList<>();

}


