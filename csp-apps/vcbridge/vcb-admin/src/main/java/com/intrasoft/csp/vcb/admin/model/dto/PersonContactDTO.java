package com.intrasoft.csp.vcb.admin.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class PersonContactDTO {

    private String id;
    private String full_name;
    private String email;
    private String email_visibility;
    private String postal_address;
    private String postal_country;
    private String ml_email;
    private String ml_key;
    private List<Object> phone_numbers;
    private List<Object> certificates;
    private List<Object> memberships;

}
