package com.eureka.store.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UsertDTO {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;
}
